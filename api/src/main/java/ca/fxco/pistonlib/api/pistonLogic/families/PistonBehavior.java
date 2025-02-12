package ca.fxco.pistonlib.api.pistonLogic.families;

/**
 * The PistonBehavior that should be used by a piston
 *
 * @author FX
 * @since 1.2.0
 */
public interface PistonBehavior {

    /**
     * Check if this is a very sticky piston.
     * This makes the front of the sticky piston stick like a slime block.
     *
     * @return {@code true} if the piston is very sticky, otherwise {@code false}
     * @since 1.2.0
     */
    boolean isVerySticky();

    /**
     * Check if this piston can be powered from the front.
     *
     * @return {@code true} if the piston can be powered from the front, otherwise {@code false}
     * @since 1.2.0
     */
    boolean isFrontPowered();

    /**
     * If this piston is quasi.
     * If it's affected by quasi-connectivity.
     *
     * @return {@code true} if the piston is affected by quasi-connectivity, otherwise {@code false}
     * @since 1.2.0
     */
    boolean isQuasi();

    /**
     * Gets the piston push limit.
     *
     * @return The push limit
     * @since 1.2.0
     */
    int getPushLimit();

    /**
     * Gets the extending speed
     *
     * @return The extending speed
     * @since 1.2.0
     */
    float getExtendingSpeed();

    /**
     * Gets the retracting speed
     *
     * @return The retracting speed
     * @since 1.2.0
     */
    float getRetractingSpeed();

    /**
     * If this piston is allowed to retract while its extending
     *
     * @return {@code true} if its able to retract while extending, otherwise {@code false}
     * @since 1.2.0
     */
    boolean isRetractOnExtending();

    /**
     * If this piston is allowed to extend while its retracting
     *
     * @return {@code true} if its able to extend while retracting, otherwise {@code false}
     * @since 1.2.0
     */
    boolean isExtendOnRetracting();

    /**
     * Gets the minimum length (in blocks) that the piston can be.
     * Vanilla is 0
     *
     * @return The minimum length
     * @since 1.2.0
     */
    int getMinLength();

    /**
     * Gets the maximum length (in blocks) that the piston can be.
     * Vanilla is 1
     *
     * @return The maximum length
     * @since 1.2.0
     */
    int getMaxLength();

    /**
     * The PistonBehavior builder
     *
     * @since 1.2.0
     */
    interface Builder {

        /**
         * Makes the front of the sticky piston stick like a slime block.
         *
         * @return The builder
         * @since 1.2.0
         */
        Builder verySticky();

        /**
         * This piston can be powered from the front.
         *
         * @return The builder
         * @since 1.2.0
         */
        Builder frontPowered();

        /**
         * If this piston shouldn't be quasi.
         * Meaning it'd no longer be affected by quasi-connectivity.
         *
         * @return The builder
         * @since 1.2.0
         */
        Builder noQuasi();

        /**
         * Sets the piston push limit for this piston.
         *
         * @param limit the push limit
         * @return The builder
         * @since 1.2.0
         */
        Builder pushLimit(int limit);

        /**
         * Sets both the extending and retracting speed for this piston
         *
         * @param generalSpeed the extending and retracting speed
         * @return The builder
         * @since 1.2.0
         */
        Builder speed(float generalSpeed);

        /**
         * Sets the extending and retracting speed for this piston
         *
         * @param extendingSpeed  the extending speed
         * @param retractingSpeed the retracting speed
         * @return The builder
         * @since 1.2.0
         */
        Builder speed(float extendingSpeed, float retractingSpeed);

        /**
         * Sets the extending speed for this piston
         *
         * @param extendingSpeed the extending speed
         * @return The builder
         * @since 1.2.0
         */
        Builder extendingSpeed(float extendingSpeed);

        /**
         * Sets the retracting speed for this piston
         *
         * @param retractingSpeed the retracting speed
         * @return The builder
         * @since 1.2.0
         */
        Builder retractingSpeed(float retractingSpeed);

        /**
         * Sets if the piston should be able to retract while extending
         *
         * @param retractOnExtending if you can retract while extending
         * @return The builder
         * @since 1.2.0
         */
        Builder retractOnExtending(boolean retractOnExtending);

        /**
         * Sets if the piston should be able to extend while retracting
         *
         * @param extendOnRetracting if you can extend while retracting
         * @return The builder
         * @since 1.2.0
         */
        Builder extendOnRetracting(boolean extendOnRetracting);

        /**
         * Sets the max length of this piston
         *
         * @param maxLength the max length
         * @return The builder
         * @since 1.2.0
         */
        Builder maxLength(int maxLength);

        /**
         * Sets the min length of this piston
         *
         * @param minLength the min length
         * @return The builder
         * @since 1.2.0
         */
        Builder minLength(int minLength);

        /**
         * Builds a {@link PistonBehavior}
         *
         * @return The new piston behavior
         * @since 1.2.0
         */
        PistonBehavior build();
    }
}
