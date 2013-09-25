package com.rrd.tools.attach;

import static org.junit.Assert.assertNotNull;

import org.junit.Test;

public class VirtualMachineTest {
	@Test
	public void testThisVM(){
		VirtualMachine myVM = VirtualMachine.getThisVM();
		assertNotNull(myVM);
		System.out.println(myVM);
	}
	@Test
	public void testInstrumentation(){
		assertNotNull(LocalInstrumentationFactory.getLocalInstrumentation());							
	}
}
