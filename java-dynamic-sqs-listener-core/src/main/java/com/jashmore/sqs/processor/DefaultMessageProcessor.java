package com.jashmore.sqs.processor;

import com.amazonaws.annotation.ThreadSafe;
import com.amazonaws.services.sqs.AmazonSQSAsync;
import com.amazonaws.services.sqs.model.DeleteMessageResult;
import com.amazonaws.services.sqs.model.Message;
import com.jashmore.sqs.QueueProperties;
import com.jashmore.sqs.argument.ArgumentResolutionException;
import com.jashmore.sqs.argument.ArgumentResolverService;
import com.jashmore.sqs.argument.acknowledge.Acknowledge;
import lombok.AllArgsConstructor;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.concurrent.Future;

/**
 * Default implementation of the {@link MessageProcessor} that will simply resolve arguments, process the message and delete the
 * message from the queue if it was completed successfully.
 */
@ThreadSafe
@AllArgsConstructor
public class DefaultMessageProcessor implements MessageProcessor {
    private final ArgumentResolverService argumentResolverService;
    private final QueueProperties queueProperties;
    private final AmazonSQSAsync amazonSqs;
    private final Method messageConsumerMethod;
    private final Object messageConsumerBean;

    @Override
    public void processMessage(final Message message) throws MessageProcessingException {
        final Parameter[] parameters = messageConsumerMethod.getParameters();

        final Object[] arguments = Arrays.stream(parameters)
                .map(parameter -> {
                    try {
                        return argumentResolverService.resolveArgument(queueProperties, parameter, message);
                    } catch (final ArgumentResolutionException argumentResolutionException) {
                        throw new MessageProcessingException("Error resolving arguments for message", argumentResolutionException);
                    }
                })
                .toArray(Object[]::new);

        try {
            messageConsumerMethod.invoke(messageConsumerBean, arguments);
            // If there is no Acknowledge argument in the method we should resolve the message if it was completed without an exception
            if (!hasAcknowledgeArgument(arguments)) {
                final Future<DeleteMessageResult> deleteMessageResultFuture = amazonSqs.deleteMessageAsync(
                        queueProperties.getQueueUrl(), message.getReceiptHandle());

                deleteMessageResultFuture.get();
            }
        } catch (final InterruptedException interruptedException) {
            throw new MessageProcessingException("Thread was interrupted while trying to delete message");
        } catch (final Throwable throwable) {
            throw new MessageProcessingException("Error processing message", throwable);
        }
    }

    private boolean hasAcknowledgeArgument(final Object... arguments) {
        return Arrays.stream(arguments)
                .anyMatch(argument -> Acknowledge.class.isAssignableFrom(argument.getClass()));
    }
}