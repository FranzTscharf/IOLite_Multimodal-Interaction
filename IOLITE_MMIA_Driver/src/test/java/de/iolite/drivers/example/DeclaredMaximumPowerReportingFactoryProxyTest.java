
package de.iolite.drivers.example;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Matchers;
import org.mockito.Mockito;

import de.iolite.drivers.basic.DriverConstants;
import de.iolite.drivers.framework.DataPoint;
import de.iolite.drivers.framework.DataPointConfiguration;
import de.iolite.drivers.framework.DataPointFactory;
import de.iolite.drivers.framework.DataPointValueCallback;
import de.iolite.drivers.framework.exception.DataPointConfigurationException;
import de.iolite.drivers.framework.exception.DataPointInstantiationException;
import de.iolite.drivers.framework.exception.IllegalValueException;

/**
 * Tests the {@link DeclaredMaximumPowerReportingFactoryProxy}.
 *
 * @author Niels Johannes
 * @author Jonathan Gruber
 * @author Grzegorz Lehmann
 * @since 17.11
 */
public class DeclaredMaximumPowerReportingFactoryProxyTest {

	private static final double DECLARED_MAXIMUM_POWER_VALUE = 5.4;

	/**
	 * Test for delegating DataPointFactory call {@code DataPointFactory.create} in case of {@code DriverConstants.PROPERTY_powerUsage_ID} being created.
	 *
	 * @throws DataPointInstantiationException unexpected
	 * @throws DataPointConfigurationException unexpected
	 */
	@Test
	public final void powerUsageDataPointCreationIsDelegated()
			throws DataPointInstantiationException, DataPointConfigurationException {
		// Mock behavior
		final DataPointConfiguration configurationMock = mock(DataPointConfiguration.class);
		when(configurationMock.optBoolean(ExampleDriver.CONFIGURATION_ESTIMATE_POWER)).thenReturn(Optional.empty());

		final DataPointFactory delegateMock = mock(DataPointFactory.class);
		when(delegateMock.create(any(), anyString(), any())).then(ignored -> mock(DataPoint.class));

		// create testee
		final DeclaredMaximumPowerReportingFactoryProxy testee =
				new DeclaredMaximumPowerReportingFactoryProxy(delegateMock, () -> Optional.of(DECLARED_MAXIMUM_POWER_VALUE));

		// execute
		testee.create(configurationMock, DriverConstants.PROPERTY_powerUsage_ID, mock(DataPointValueCallback.class));

		// expect the delegate to be called
		verify(delegateMock).create(any(), anyString(), any());
	}

	/**
	 * Test for power usage value being reported to callback in case of on/off property being true.
	 *
	 * @throws DataPointInstantiationException unexpected
	 * @throws DataPointConfigurationException unexpected
	 * @throws IllegalValueException unexpected
	 */
	@Test
	public final void declaredMaximumPowerValueIsReportedWhenOnIsTrue()
			throws DataPointInstantiationException, DataPointConfigurationException, IllegalValueException {
		// Mock behavior
		final DataPointConfiguration powerConfigurationMock = mock(DataPointConfiguration.class);
		when(powerConfigurationMock.optBoolean(ExampleDriver.CONFIGURATION_ESTIMATE_POWER)).thenReturn(Optional.of(true));

		// we must intercept the value callback that the factory will provide to the on/off data point, so we can simulate on=true
		final ArgumentCaptor<DataPointValueCallback> callbackCaptor = ArgumentCaptor.forClass(DataPointValueCallback.class);
		final DataPointFactory onOffFactoryMock = mock(DataPointFactory.class);
		when(onOffFactoryMock.create(any(), anyString(), callbackCaptor.capture())).thenReturn(mock(DataPoint.class));

		// create factory
		final DeclaredMaximumPowerReportingFactoryProxy testee =
				new DeclaredMaximumPowerReportingFactoryProxy(onOffFactoryMock, () -> Optional.of(DECLARED_MAXIMUM_POWER_VALUE));
		// both power usage and on/off data points must exist
		final DataPointValueCallback powerUsageCallbackMock = mock(DataPointValueCallback.class);
		testee.create(powerConfigurationMock, DriverConstants.PROPERTY_powerUsage_ID, powerUsageCallbackMock);
		testee.create(mock(DataPointConfiguration.class), DriverConstants.PROPERTY_on_ID, mock(DataPointValueCallback.class));

		// simulate as if on/off data point reported true
		callbackCaptor.getValue().newBooleanValue(true);

		// power usage should be reported
		verify(powerUsageCallbackMock).newDoubleValue(DECLARED_MAXIMUM_POWER_VALUE);
	}

	/**
	 * Test for power usage value, lineary scaling with dimming level, being reported to callback in case of on/off property being true and dimming level
	 * property exists.
	 *
	 * @throws DataPointInstantiationException unexpected
	 * @throws DataPointConfigurationException unexpected
	 * @throws IllegalValueException unexpected
	 */
	@Test
	public final void powerUsageEstimateScalesLinearlyWithDimmingLevel()
			throws DataPointInstantiationException, DataPointConfigurationException, IllegalValueException {
		// Mock behavior
		final DataPointConfiguration powerConfigurationMock = mock(DataPointConfiguration.class);
		when(powerConfigurationMock.optBoolean(ExampleDriver.CONFIGURATION_ESTIMATE_POWER)).thenReturn(Optional.of(true));

		// we must intercept the value callback that the factory will provide to the on/off data point, so we can simulate on=true
		final ArgumentCaptor<DataPointValueCallback> onCallbackCaptor = ArgumentCaptor.forClass(DataPointValueCallback.class);
		final ArgumentCaptor<DataPointValueCallback> dimmingLevelCallbackCaptor = ArgumentCaptor.forClass(DataPointValueCallback.class);
		final DataPointFactory delegateMock = mock(DataPointFactory.class);
		when(delegateMock.create(any(), Matchers.eq(DriverConstants.PROPERTY_on_ID), onCallbackCaptor.capture())).thenReturn(mock(DataPoint.class));
		when(delegateMock.create(any(), Matchers.eq(DriverConstants.PROPERTY_dimmingLevel_ID), dimmingLevelCallbackCaptor.capture())).thenReturn(
				mock(DataPoint.class));

		// create factory
		final DeclaredMaximumPowerReportingFactoryProxy testee =
				new DeclaredMaximumPowerReportingFactoryProxy(delegateMock, () -> Optional.of(DECLARED_MAXIMUM_POWER_VALUE));
		// both power usage and on/off data points must exist
		final DataPointValueCallback powerUsageCallbackMock = mock(DataPointValueCallback.class);
		testee.create(powerConfigurationMock, DriverConstants.PROPERTY_powerUsage_ID, powerUsageCallbackMock);
		testee.create(mock(DataPointConfiguration.class), DriverConstants.PROPERTY_on_ID, mock(DataPointValueCallback.class));
		testee.create(mock(DataPointConfiguration.class), DriverConstants.PROPERTY_dimmingLevel_ID, mock(DataPointValueCallback.class));

		// simulate as if on/off data point reported true
		onCallbackCaptor.getValue().newBooleanValue(true);

		Mockito.reset(powerUsageCallbackMock);
		dimmingLevelCallbackCaptor.getValue().newIntValue(50);

		// power usage should be reported
		verify(powerUsageCallbackMock, Mockito.only()).newDoubleValue(DECLARED_MAXIMUM_POWER_VALUE / 2);
	}
}
