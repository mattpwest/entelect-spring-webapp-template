package za.co.entelect.domain.entities.history;

/**
 * These roles are based on constructing history messages from the viewpoint of each of the different entities involved
 * in an event.
 *
 * Example event:
 *      Event: MANAGER_ADDED_TO_PROJECT
 *      Actor: Project owner user
 *      Subject: New manager user
 *      Object: Project
 *
 * Example messages:
 *      Project owner: Added {subject} as manager of {object}.
 *      Subject: Added as manager of {object} by {actor}.
 *      Object: {subject} added as manager by {actor}.
 */
public enum HistoryEntityRole {
    ACTOR,
    SUBJECT,
    OBJECT
}
