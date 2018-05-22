
package de.iolite.drivers.example;

import java.util.function.Consumer;

import org.junit.Test;
import org.mockito.Mockito;

import de.iolite.drivers.framework.DataPointValueCallback;
import de.iolite.drivers.framework.exception.IllegalValueException;

/**
 * Tests the {@link BooleanValueForkingCallbackDecorator}.
 *
 * @author Niels Johannes
 * @author Jonathan Gruber
 * @since 17.11
 */
public class BooleanValueForkingCallbackDecoratorTest {

	/**
	 * Expect false boolean values being passed to consumer.
	 *
	 * @throws IllegalValueException unexpected
	 */
	@Test
	public final void falseValuesArePassedToConsumer()
			throws IllegalValueException {
		final DataPointValueCallback delegateMock = Mockito.mock(DataPointValueCallback.class);
		final Consumer<Boolean> onValueConsumer = Mockito.mock(Consumer.class);

		final BooleanValueForkingCallbackDecorator testee = new BooleanValueForkingCallbackDecorator(delegateMock, onValueConsumer);

		// execute code
		testee.newBooleanValue(false);

		// test execution
		Mockito.verify(onValueConsumer).accept(false);
	}

	/**
	 * Expect {@code newBooleanValue(Boolean)} are passed to delegated {@link DataPointValueCallback}.
	 * @throws IllegalValueException unexpected
	 */
	@Test
	public final void newBooleanValueBooleanCallsArePassedToDelegate()
			throws IllegalValueException {
		final DataPointValueCallback delegateMock = Mockito.mock(DataPointValueCallback.class);
		final Consumer<Boolean> onValueConsumer = Mockito.mock(Consumer.class);

		final BooleanValueForkingCallbackDecorator testee = new BooleanValueForkingCallbackDecorator(delegateMock, onValueConsumer);

		// execute code
		testee.newBooleanValue(true);

		// test execution
		Mockito.verify(delegateMock).newBooleanValue(true);
	}

	/**
	 * Expect {@code newBooleanValue(Boolean, Timestamp)} are passed to delegated {@link DataPointValueCallback}.
	 * @throws IllegalValueException unexpected
	 */
	@Test
	public final void newBooleanValueBooleanLongCallsArePassedToDelegate()
			throws IllegalValueException {
		final DataPointValueCallback delegateMock = Mockito.mock(DataPointValueCallback.class);
		final Consumer<Boolean> onValueConsumer = Mockito.mock(Consumer.class);

		final BooleanValueForkingCallbackDecorator testee = new BooleanValueForkingCallbackDecorator(delegateMock, onValueConsumer);

		// execute code
		final long timestamp = System.currentTimeMillis();
		testee.newBooleanValue(true, timestamp);

		// test execution
		Mockito.verify(delegateMock).newBooleanValue(true, timestamp);
	}

	/**
	 * Expect true boolean values being passed to consumer.
	 * @throws IllegalValueException unexpected
	 */
	@Test
	public final void trueValuesArePassedToConsumer()
			throws IllegalValueException {
		final DataPointValueCallback delegateMock = Mockito.mock(DataPointValueCallback.class);
		final Consumer<Boolean> onValueConsumer = Mockito.mock(Consumer.class);

		final BooleanValueForkingCallbackDecorator testee = new BooleanValueForkingCallbackDecorator(delegateMock, onValueConsumer);

		// execute code
		testee.newBooleanValue(true);

		// test execution
		Mockito.verify(onValueConsumer).accept(true);
	}
}
