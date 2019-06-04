package com.jashmore.sqs.broker.concurrent;

import static com.jashmore.sqs.broker.concurrent.ConcurrentMessageBrokerConstants.DEFAULT_CONCURRENCY_POLLING_IN_MS;

import com.google.common.base.Preconditions;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.ToString;
import net.jcip.annotations.ThreadSafe;

import java.util.Locale;
import java.util.Optional;
import javax.annotation.Nullable;
import javax.validation.constraints.Min;
import javax.validation.constraints.PositiveOrZero;

/**
 * Implementation that stores the value as non-mutable field values and therefore will return the same value on every call.
 *
 * <p>This is useful when you don't need the listener to be dynamic or have the ability to turn off when needed.
 *
 * <p>This implementation is thread safe, even though it doesn't need to be, due to it only returning immutable values.
 */
@ToString
@EqualsAndHashCode
@Builder(toBuilder = true)
@ThreadSafe
public final class StaticConcurrentMessageBrokerProperties implements ConcurrentMessageBrokerProperties {
    @NonNull
    private final Integer concurrencyLevel;
    @NonNull
    private final Long preferredConcurrencyPollingRateInMilliseconds;
    private final String threadNameFormat;
    private final Long errorBackoffTimeInMilliseconds;

    @SuppressFBWarnings("RV_RETURN_VAL")
    public StaticConcurrentMessageBrokerProperties(final Integer concurrencyLevel,
                                                   final Long preferredConcurrencyPollingRateInMilliseconds,
                                                   final String threadNameFormat,
                                                   final Long errorBackoffTimeInMilliseconds) {
        Preconditions.checkArgument(concurrencyLevel == null || concurrencyLevel >= 0, "concurrencyLevel should be greater than or equal to zero");
        Preconditions.checkArgument(preferredConcurrencyPollingRateInMilliseconds == null || preferredConcurrencyPollingRateInMilliseconds >= 0,
                "preferredConcurrencyPollingRateInMilliseconds should be greater than or equal to zero");

        this.concurrencyLevel = Optional.ofNullable(concurrencyLevel)
                .orElse(0);
        this.preferredConcurrencyPollingRateInMilliseconds = Optional.ofNullable(preferredConcurrencyPollingRateInMilliseconds)
                .orElse(DEFAULT_CONCURRENCY_POLLING_IN_MS);
        this.errorBackoffTimeInMilliseconds = errorBackoffTimeInMilliseconds;

        this.threadNameFormat = threadNameFormat;
        if (threadNameFormat != null) {

            // Test that the thread name is in the correct format
            //noinspection ResultOfMethodCallIgnored
            String.format(Locale.ROOT, threadNameFormat, 1);
        }
    }

    @Override
    public @Min(0) Integer getConcurrencyLevel() {
        return concurrencyLevel;
    }

    @Override
    public @Min(0) Long getPreferredConcurrencyPollingRateInMilliseconds() {
        return preferredConcurrencyPollingRateInMilliseconds;
    }

    @Override
    public String getThreadNameFormat() {
        return threadNameFormat;
    }

    @Nullable
    @Override
    public @PositiveOrZero Long getErrorBackoffTimeInMilliseconds() {
        return errorBackoffTimeInMilliseconds;
    }
}