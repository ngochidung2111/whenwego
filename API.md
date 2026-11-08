# WhenWeGo -- API Implementation Checklist

> **Tech stack:** Java + Spring Boot, ReactJS + Vite, Tailwind CSS,
> MySQL\
> **API base path:** `/api/v1`\
> **Format:** REST + JSON\
> **Authentication:** JWT for registered users; event-scoped guest token
> for guests\
> **Conventions:** UUID identifiers, `LocalDate`/`YYYY-MM-DD` for event
> dates, ISO-8601 timestamps for instants. Store instants in UTC.

This document lists the APIs in the recommended implementation order.
Build and test each phase before moving to the next. Endpoint names are
a proposed v1 contract and can be adjusted during API design.

------------------------------------------------------------------------

## 0. API conventions and shared rules

-   Use `Authorization: Bearer <access_token>` for authenticated member
    endpoints.
-   Guest endpoints use a scoped guest token; store only token hashes
    server-side.
-   Use consistent response envelopes and error formats across
    controllers.
-   Enforce event membership and roles in Spring Security and the
    service layer.
-   Validate ownership and event boundaries on every resource access.
-   Use pagination for collection endpoints where appropriate.
-   Keep `No response` distinct from `Unavailable`; absence of an
    availability response means `No response`.
-   Suggested common error statuses: `400` validation, `401`
    unauthenticated, `403` unauthorized, `404` not found, `409`
    state/conflict, `429` rate limited.
-   Publish the contract with OpenAPI/Swagger.

------------------------------------------------------------------------

## Phase 1 -- Identity, invitations, and event foundation

### 1. Authentication

  Method   Endpoint           Purpose
  -------- ------------------ ------------------------------------
  POST     `/api/auth/register`   Create a registered account and issue a JWT
  POST     `/api/auth/login`      Authenticate and issue a JWT
  POST     `/auth/refresh`    Refresh an access token
  POST     `/auth/logout`     Revoke the refresh session/token
  GET      `/auth/me`         Get the current authenticated user

**Implementation notes** - The current implementation uses BCrypt and
stateless JWT bearer access tokens. Configure `JWT_SECRET` with a
cryptographically random secret (at least 32 bytes; Base64 is supported)
and send it as `Authorization: Bearer <accessToken>`. Access tokens
expire after `app.jwt.expiration` milliseconds (the default is 24 hours).
The `USER` role is assigned during registration; additional endpoint
authorization can use `@PreAuthorize("hasRole('ADMIN')")`. Refresh and
logout endpoints are not implemented yet.

### 2. User profile

  Method   Endpoint               Purpose
  -------- ---------------------- --------------------------------
  GET      `/users/me`            Get the current user's profile
  PUT      `/users/me`            Update profile details
  PATCH    `/users/me/password`   Change password

### 3. Invitations

  ---------------------------------------------------------------------------------
  Method                  Endpoint                          Purpose
  ----------------------- --------------------------------- -----------------------
  POST                    `/events/{eventId}/invitations`   Create an invitation
                                                            link

  GET                     `/invitations/{token}`            Preview invitation and
                                                            event information

  POST                    `/invitations/{token}/accept`     Accept invitation as a
                                                            registered user
  ---------------------------------------------------------------------------------

**Implementation notes** - Invitation tokens should be random,
unguessable, expirable, and revocable. - Persist token hashes rather
than raw invitation secrets. - Validate event state and invitation
status before accepting.

### 4. Guest access

  ----------------------------------------------------------------------------
  Method                  Endpoint                     Purpose
  ----------------------- ---------------------------- -----------------------
  POST                    `/events/{eventId}/guests`   Join an event as a
                                                       guest

  POST                    `/guest-auth/exchange`       Exchange a valid guest
                                                       credential for a scoped
                                                       guest token

  GET                     `/guest-auth/me`             Get the current guest
                                                       identity and event
                                                       scope

  POST                    `/guest-auth/claim`          Link/claim a guest
                                                       identity after
                                                       registering or logging
                                                       in
  ----------------------------------------------------------------------------

**Implementation notes** - Guest access must be limited to the relevant
event and allowed guest actions. - Define a safe guest recovery/claim
process to avoid duplicate identities. - Do not allow a guest token to
access unrelated events or member-only functions.

### 5. Events

  ----------------------------------------------------------------------------
  Method                  Endpoint                     Purpose
  ----------------------- ---------------------------- -----------------------
  POST                    `/events`                    Create an event

  GET                     `/events`                    List events visible to
                                                       the current user

  GET                     `/events/{eventId}`          Get event details

  PUT                     `/events/{eventId}`          Update event details

  PATCH                   `/events/{eventId}/status`   Change event lifecycle
                                                       status

  DELETE                  `/events/{eventId}`          Cancel/archive or
                                                       delete according to the
                                                       agreed lifecycle policy
  ----------------------------------------------------------------------------

**Implementation notes** - Event lifecycle:
`DRAFT → COLLECTING_AVAILABILITY → VOTING → CONFIRMED → PLANNING → COMPLETED`;
`CANCELLED` is an alternate terminal state. - The Smart Date Finder is a
computation, not an event lifecycle state. - Store the event timezone as
an IANA timezone identifier.

### 6. Event members

  ----------------------------------------------------------------------------------------
  Method                  Endpoint                                 Purpose
  ----------------------- ---------------------------------------- -----------------------
  GET                     `/events/{eventId}/members`              List event members and
                                                                   guests

  DELETE                  `/events/{eventId}/members/me`           Leave an event

  DELETE                  `/events/{eventId}/members/{memberId}`   Host removes a
                                                                   member/guest
  ----------------------------------------------------------------------------------------

**Phase 1 exit criteria** - A user can register, log in, and view their
profile. - A host can create an event and invite registered users. - An
invitee can join as a registered member or guest. - Event access is
correctly restricted by membership and role.

------------------------------------------------------------------------

## Phase 2 -- Availability, date finding, and group decision

### 7. Availability

  ------------------------------------------------------------------------------------------
  Method                  Endpoint                                   Purpose
  ----------------------- ------------------------------------------ -----------------------
  GET                     `/events/{eventId}/availability`           Get availability
                                                                     responses for the event

  GET                     `/availability/me?eventId={eventId}`       Get the current
                                                                     user's/guest's
                                                                     availability

  PUT                     `/events/{eventId}/availability/me`        Create or replace
                                                                     availability for the
                                                                     current participant

  GET                     `/events/{eventId}/availability/summary`   Get availability counts
                                                                     and calendar summary
  ------------------------------------------------------------------------------------------

**Availability values** - `AVAILABLE` - `MAYBE` - `UNAVAILABLE` -
`NO_RESPONSE` is a derived state when no response exists; it is not
submitted as a response.

**Implementation notes** - Validate submitted dates against the event's
allowed date range. - Preserve the distinction between a missing
response and an explicit unavailable response. - Define whether updates
are allowed after the availability deadline and enforce the rule
consistently.

### 8. Smart Date Finder

  ----------------------------------------------------------------------------------------------
  Method                  Endpoint                                       Purpose
  ----------------------- ---------------------------------------------- -----------------------
  POST                    `/events/{eventId}/date-finder/runs`           Run the date-finding
                                                                         algorithm

  GET                     `/events/{eventId}/date-finder/runs`           List previous finder
                                                                         runs

  GET                     `/events/{eventId}/date-finder/runs/{runId}`   Get a run and its
                                                                         ranked date windows
  ----------------------------------------------------------------------------------------------

**Request should include** - Requested consecutive duration (number of
days). - Date range, if not already fixed by the event. - Ranking mode:
`MAXIMUM_ATTENDANCE`, `MAXIMUM_CERTAINTY`, `FULL_ATTENDANCE`, or
`BALANCED`. - Optional minimum attendance threshold.

**Implementation notes** - Snapshot the active participant cohort and
availability data for each run. - Return the ranking metrics and a
human-readable explanation for each suggested window. - Do not
automatically create a poll or confirm a date. - Return explicit
outcomes such as `NO_PARTICIPANTS` and `RANGE_TOO_SHORT`. - Treat
expected attendance score as a ranking heuristic, not a statistically
calibrated probability.

### 9. Polls

  ---------------------------------------------------------------------------
  Method                  Endpoint                    Purpose
  ----------------------- --------------------------- -----------------------
  POST                    `/events/{eventId}/polls`   Create a date poll
                                                      using selected date
                                                      options

  GET                     `/events/{eventId}/polls`   List polls for an event

  GET                     `/polls/{pollId}`           Get poll details and
                                                      options

  PATCH                   `/polls/{pollId}`           Edit a draft poll

  POST                    `/polls/{pollId}/open`      Open the poll for
                                                      voting

  POST                    `/polls/{pollId}/close`     Close the poll
  ---------------------------------------------------------------------------

**Implementation notes** - Poll lifecycle: `DRAFT → OPEN → CLOSED` or
`EXPIRED`. - Each date option must represent a valid consecutive date
window. - Enforce the event's poll deadline and allowed lifecycle
transitions.

### 10. Voting

  ----------------------------------------------------------------------------
  Method                  Endpoint                     Purpose
  ----------------------- ---------------------------- -----------------------
  PUT                     `/polls/{pollId}/votes/me`   Create or replace the
                                                       current participant's
                                                       vote

  GET                     `/polls/{pollId}/results`    Get vote totals and
                                                       poll results
  ----------------------------------------------------------------------------

**Implementation notes** - Enforce one current vote per participant per
poll (updates replace the previous vote). - Validate that the selected
option belongs to the specified poll. - Apply the agreed quorum and
tie-breaking rules when calculating results. - A vote result is not
itself confirmation of the event date.

### 11. Date confirmation

  ------------------------------------------------------------------------------------
  Method                  Endpoint                             Purpose
  ----------------------- ------------------------------------ -----------------------
  POST                    `/events/{eventId}/confirm-date`     Host confirms the
                                                               selected date option

  POST                    `/events/{eventId}/reopen-voting`    Reopen or initiate a
                                                               new voting round when
                                                               permitted

  GET                     `/events/{eventId}/confirmed-date`   Get the confirmed date
                                                               window
  ------------------------------------------------------------------------------------

**Implementation notes** - Require the host's authorization to
confirm. - Record who confirmed and when. - If the confirmed date
changes, require a new poll or explicit reconfirmation according to the
event rules. - Never auto-confirm based only on Smart Date Finder
output.

**Phase 2 exit criteria** - Participants can submit and update
availability. - The host can run the finder and inspect explainable
ranked results. - The host can create a poll; participants can vote;
results are visible. - The host can explicitly confirm a date.

------------------------------------------------------------------------

## Phase 3 -- Planning and supporting capabilities

### 12. Activities / basic planning

  ---------------------------------------------------------------------------------------------
  Method                  Endpoint                                      Purpose
  ----------------------- --------------------------------------------- -----------------------
  POST                    `/events/{eventId}/activities`                Create an activity

  GET                     `/events/{eventId}/activities`                List activities

  GET                     `/events/{eventId}/activities/{activityId}`   Get activity details

  PUT                     `/events/{eventId}/activities/{activityId}`   Update an activity

  DELETE                  `/events/{eventId}/activities/{activityId}`   Delete an activity
  ---------------------------------------------------------------------------------------------

**Suggested activity fields** - Title/name. - Event day or date. -
Start/end time (optional). - Notes. - Sort order.

### 13. Notifications

  ----------------------------------------------------------------------------------------
  Method                  Endpoint                                 Purpose
  ----------------------- ---------------------------------------- -----------------------
  GET                     `/notifications`                         List notifications for
                                                                   the current identity

  GET                     `/notifications/unread-count`            Get unread notification
                                                                   count

  PATCH                   `/notifications/{notificationId}/read`   Mark one notification
                                                                   as read

  POST                    `/notifications/read-all`                Mark all notifications
                                                                   as read
  ----------------------------------------------------------------------------------------

**Implementation notes** - Verify the recipient owns the notification. -
Trigger notifications for key events such as invitation, availability
deadline, poll opened/closed, and date confirmation. - Decide whether
guest notifications are in-app only or deferred from MVP.

### 14. System health and API documentation

  Method   Endpoint                   Purpose
  -------- -------------------------- --------------------------
  GET      `/actuator/health`         Application health check
  GET      `/v3/api-docs`             OpenAPI specification
  GET      `/swagger-ui/index.html`   Swagger UI

**Implementation notes** - Expose only appropriate Actuator endpoints
outside local development. - Keep API documentation synchronized with
implemented request/response DTOs.

------------------------------------------------------------------------

## Recommended implementation sequence

Use this checklist to track delivery. Check an item only after its
endpoint, authorization, validation, and automated tests are complete.

### Phase 1

-   [ ] 1. Authentication
-   [ ] 2. User profile
-   [ ] 3. Invitations
-   [ ] 4. Guest access
-   [ ] 5. Events
-   [ ] 6. Event members

### Phase 2

-   [ ] 7. Availability
-   [ ] 8. Smart Date Finder
-   [ ] 9. Polls
-   [ ] 10. Voting
-   [ ] 11. Date confirmation

### Phase 3

-   [ ] 12. Activities / basic planning
-   [ ] 13. Notifications
-   [ ] 14. System health and API documentation

------------------------------------------------------------------------

## Cross-cutting engineering checklist

-   [ ] Define request/response DTOs; do not expose JPA entities
    directly.
-   [ ] Add Bean Validation to request DTOs.
-   [ ] Implement centralized exception handling and a consistent error
    response.
-   [ ] Add authorization tests for host, member, guest, and admin
    roles.
-   [ ] Add integration tests for important lifecycle transitions.
-   [ ] Add database constraints and indexes through Flyway migrations.
-   [ ] Ensure vote options belong to the same poll as the vote.
-   [ ] Ensure a notification has exactly one intended recipient
    identity.
-   [ ] Handle guest-to-registered-account claiming without duplicate
    event participants.
-   [ ] Add idempotency or conflict handling where clients may retry
    state-changing requests.
-   [ ] Document pagination, sorting, filtering, and API versioning
    conventions.

## Suggested MVP build order

If implementation capacity is limited, complete the end-to-end core
journey first:

**Register/Login → Create Event → Invite/Join (member or guest) → Submit
Availability → Run Date Finder → Create Poll → Vote → Confirm Date.**

Then add basic activities and notifications. Defer chat, photo albums,
payments, calendar sync, maps/bookings, and AI itinerary features until
after the core journey is validated.
