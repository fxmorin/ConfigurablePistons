package ca.fxco.pistonlib.pistonLogic.families;

import ca.fxco.pistonlib.PistonLibConfig;
import ca.fxco.pistonlib.api.pistonLogic.families.PistonBehavior;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PistonBehaviorImpl implements PistonBehavior {

    public static final PistonBehavior DEFAULT = PistonBehaviorImpl.builder().build();

    private final boolean verySticky;
    private final boolean frontPowered;
    private final boolean quasi;
    private final int pushLimit;
    private final float extendingSpeed;
    private final float retractingSpeed;
    private final boolean retractOnExtending;
    private final boolean extendOnRetracting;
    private final int minLength;
    private final int maxLength;

    /**
     * Create a builder for {@link PistonBehaviorImpl}
     *
     * @return A {@link PistonBehaviorImpl.Builder} builder
     */
    public static PistonBehavior.Builder builder() {
        return new PistonBehaviorImpl.Builder();
    }

    /**
     * A builder for {@link PistonBehaviorImpl}
     */
    public static class Builder implements PistonBehavior.Builder {

        protected boolean verySticky = false;
        protected boolean frontPowered = false;
        protected boolean quasi = true;
        protected int pushLimit = PistonLibConfig.defaultPistonPushLimit;
        protected float extendingSpeed = 1;
        protected float retractingSpeed = 1;
        protected boolean retractOnExtending = true;
        protected boolean extendOnRetracting;
        protected int minLength = 0;
        protected int maxLength = 1;

        @Override
        public Builder verySticky() {
            this.verySticky = true;
            return this;
        }

        @Override
        public Builder frontPowered() {
            this.frontPowered = true;
            return this;
        }

        @Override
        public Builder noQuasi() {
            this.quasi = false;
            return this;
        }

        @Override
        public Builder pushLimit(int limit) {
            this.pushLimit = limit;
            return this;
        }

        @Override
        public Builder speed(float generalSpeed) {
            this.extendingSpeed = generalSpeed;
            this.retractingSpeed = generalSpeed;
            return this;
        }

        @Override
        public Builder speed(float extendingSpeed, float retractingSpeed) {
            this.extendingSpeed = extendingSpeed;
            this.retractingSpeed = retractingSpeed;
            return this;
        }

        @Override
        public Builder extendingSpeed(float extendingSpeed) {
            this.extendingSpeed = extendingSpeed;
            return this;
        }

        @Override
        public Builder retractingSpeed(float retractingSpeed) {
            this.retractingSpeed = retractingSpeed;
            return this;
        }

        @Override
        public Builder retractOnExtending(boolean retractOnExtending) {
            this.retractOnExtending = retractOnExtending;
            return this;
        }

        @Override
        public Builder extendOnRetracting(boolean extendOnRetracting) {
            this.extendOnRetracting = extendOnRetracting;
            return this;
        }

        @Override
        public Builder maxLength(int maxLength) {
            this.maxLength = maxLength;
            return this;
        }

        @Override
        public Builder minLength(int minLength) {
            this.minLength = minLength;
            return this;
        }

        @Override
        public PistonBehavior build() {
            return new PistonBehaviorImpl(verySticky, frontPowered, quasi, pushLimit, extendingSpeed,
                    retractingSpeed, retractOnExtending, extendOnRetracting, minLength, maxLength);
        }
    }
}
