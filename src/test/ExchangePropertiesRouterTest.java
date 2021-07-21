/*
 * Copyright 2010 Aalto University, ComNet
 * Released under GPLv3. See LICENSE.txt for details.
 */
package test;

import core.Connection;
import core.Message;
import core.Settings;
import core.VBRConnection;
import routing.ExchangePropertiesRouter;
import routing.MessageRouter;
import routing.ProphetRouter;
import util.Tuple;

import java.util.ArrayList;

/**
 * Tests for PRoPHET router
 */
public class ExchangePropertiesRouterTest extends AbstractRouterTest {

	private static int SECONDS_IN_TIME_UNIT = 60;

	@Override
	public void setUp() throws Exception {
		ts.setNameSpace(null);
		ts.putSetting(MessageRouter.B_SIZE_S, ""+BUFFER_SIZE);
		ts.putSetting(ExchangePropertiesRouter.PROPHET_NS + "." +
				ExchangePropertiesRouter.SECONDS_IN_UNIT_S , SECONDS_IN_TIME_UNIT+"");
		setRouterProto(new ExchangePropertiesRouter(ts));
		super.setUp();
	}

	/**
	 * Tests normal routing
	 */
	public void testRouting() {
		//create connection for 1->2, 2->3, 1->3
		VBRConnection vbrConnection1 = new VBRConnection(h1, new TestInterface(ts), h2, new TestInterface(ts));
		VBRConnection vbrConnection2 = new VBRConnection(h2, new TestInterface(ts), h1, new TestInterface(ts));
		VBRConnection vbrConnection3 = new VBRConnection(h1, new TestInterface(ts), h3, new TestInterface(ts));

		ExchangePropertiesRouter r1 = (ExchangePropertiesRouter)h1.getRouter();
		ExchangePropertiesRouter r2 = (ExchangePropertiesRouter)h2.getRouter();
		ExchangePropertiesRouter r3 = (ExchangePropertiesRouter)h3.getRouter();

		//open connection for 1->2 and 2->1
		clock.advance(3);
		r1.changedConnection(vbrConnection1);
		assertEquals(1, r1.getWorstCaseTransmissionOverHeadForContactProperties().size());
		assertEquals(new Long(0), r1.getWorstCaseTransmissionOverHeadForContactProperties().get(0).getValue());

		r2.changedConnection(vbrConnection2);
		assertEquals(1, r2.getWorstCaseTransmissionOverHeadForContactProperties().size());
		assertEquals(new Long(43), r2.getWorstCaseTransmissionOverHeadForContactProperties().get(0).getValue());

		//open connection for 1->3
		clock.advance(2);
		r3.changedConnection(vbrConnection3);

		//close connection for 1->3
		clock.advance(10);
		vbrConnection3.setUpState(false);
		r3.changedConnection(vbrConnection3);

		//close connection for 1->2 and 2->1
		clock.advance(5);
		vbrConnection1.setUpState(false);
		r1.changedConnection(vbrConnection1);
		vbrConnection2.setUpState(false);
		r2.changedConnection(vbrConnection2);

		assertEquals(h1.getContactPropertiesMap().size(), 1);
		assertNotNull(h1.getContactPropertiesMap().get("h1<->h2"));
		assertEquals(h1.getStorageAndEnergyPropertiesMap().size(), 1);
		assertNotNull(h1.getStorageAndEnergyPropertiesMap().get("h2"));

	}

}
