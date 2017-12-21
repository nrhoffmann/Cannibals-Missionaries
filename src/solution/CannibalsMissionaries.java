package solution;

import java.util.*;

public class CannibalsMissionaries {
    private static final Stack<State> STACK = new Stack<>();
    private static int solutionCounter = 0;

    static {
        State _3CannibalsAnd3Missionaries = new State(3, 3, true);
        STACK.push(_3CannibalsAnd3Missionaries);
    }

    public static void Solve() {
        boolean hasNextState;
        boolean nextStateIsTargetState;
        while (!STACK.empty()) {
            hasNextState = tryNextState();
            nextStateIsTargetState = isAtTargetState();

            if (nextStateIsTargetState)
                printStack();

            if (! hasNextState || nextStateIsTargetState)
                backtrack();
        }
    }

    private static boolean isAtTargetState() {
        State current = STACK.peek();

        return current.missionaries == 0 &&
                current.cannibals == 0 &&
                ! current.boat;
    }

    private static void backtrack() {
        do State.ALL_STATES.remove(STACK.pop());
        while (! STACK.empty() &&
               ! STACK.peek().hasNextTransition());
    }

    private static boolean tryNextState() {
        State next, current = STACK.peek();

        while (current.hasNextTransition()) {
                next = State.fromCurrentAndTransition(current, current.nextTransition());
                if (next != null) {
                    STACK.push(next);
                    return true;
                }
        }

        return false;
    }

    private static void printStack() {
        StringBuilder sb = new StringBuilder(String.format("Start of solution %d%n", ++solutionCounter));
        int stepNumber = 0;
        for (State step : STACK) {
            sb.append(String.format("Step %d: %s%n", ++stepNumber, step));

        }
        sb.append("End of solution");

        System.out.println(sb);
    }

    private enum StateTransition {C, CC, M, MM, MC}

    private static class State {
        private static final Set<State> ALL_STATES = new HashSet<>();

        private int cannibals, missionaries, indexOfNextStateTransition = 0;
        private boolean boat;

        private State(int cannibals, int missionaries, boolean boat) {
            setCannibals(cannibals);
            setMissionaries(missionaries);
            setBoat(boat);

            boolean cannibalsWillEatMissionaries = isCannibalsGreaterThanMissionariesOnAnySide();
            if (cannibalsWillEatMissionaries)
                throw new IllegalStateException("Cannibals will eat missionary");


            if(! ALL_STATES.add(this))
                throw new IllegalStateException(String.format("%s already exists", this));
        }

        public void setCannibals(int cannibals) {
            if (cannibals > 3 || cannibals < 0)
                throw new IllegalArgumentException("The number of cannibals must be within the range of 0-3");
            this.cannibals = cannibals;
        }

        public void setMissionaries(int missionaries) {
            if (missionaries > 3 || missionaries < 0)
                throw new IllegalArgumentException("The number of missionaries must be within the range of 0-3");
            this.missionaries = missionaries;
        }

        public void setBoat(boolean boat) {
            this.boat = boat;
        }

        private StateTransition nextTransition() {
            return StateTransition.values()[indexOfNextStateTransition++];
        }

        private boolean hasNextTransition() {
            return indexOfNextStateTransition < StateTransition.values().length;
        }

        private boolean isCannibalsGreaterThanMissionariesOnAnySide() {
            return (cannibals > missionaries && missionaries != 0) ||
                    (cannibals < missionaries && missionaries != 3);
        }

        @Override
        public String toString() {
            return String.format("State:{C: %d, M: %d, B: %b}", cannibals, missionaries, boat);
        }

        @Override
        public int hashCode() {
            return Objects.hash(cannibals, missionaries, boat);
        }

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof State) {
                State that = (State) obj;
                return cannibals == that.cannibals &&
                        missionaries == that.missionaries &&
                        boat == that.boat;
            }
            return false;
        }

        private static State fromCurrentAndTransition(State previous, StateTransition transition) {
            try {
                switch (transition) {
                    case C:
                        return new State(previous.cannibals + (previous.boat ? -1 : 1),
                                previous.missionaries,
                                !previous.boat);

                    case CC:
                        return new State(previous.cannibals + (previous.boat ? -2 : 2),
                                previous.missionaries,
                                !previous.boat);

                    case M:
                        return new State(previous.cannibals,
                                previous.missionaries + (previous.boat ? -1 : 1),
                                !previous.boat);

                    case MM:
                        return new State(previous.cannibals,
                                previous.missionaries + +(previous.boat ? -2 : 2),
                                !previous.boat);

                    case MC:
                        return new State(previous.cannibals + (previous.boat ? -1 : 1),
                                previous.missionaries + (previous.boat ? -1 : 1),
                                !previous.boat);
                    default:
                        throw new IllegalArgumentException(
                                String.format("The transition must be one of the following:" +
                                        " %s, %s, %s, %s, or %s.", (Object[]) StateTransition.values()));
                }
            } catch (Exception ex){
                return null;
            }
        }
    }
}
