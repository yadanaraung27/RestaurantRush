package src.staff;

import src.exception.InvalidActionException;

public enum StaffAction {
    SEAT,
    TAKE_ORDER,
    SERVE,
    PREPARE,
    WAIT;

    public static StaffAction parse(String command) throws InvalidActionException {
        if (command == null) {
            throw new InvalidActionException("Enter an action or 'wait'.");
        }
        return switch (command.trim().toLowerCase()) {
            case "seat" -> SEAT;
            case "order" -> TAKE_ORDER;
            case "serve" -> SERVE;
            case "prepare" -> PREPARE;
            case "wait" -> WAIT;
            default -> throw new InvalidActionException("Unknown action '" + command + "'. Use seat, order, serve, prepare, or wait.");
        };
    }
}
