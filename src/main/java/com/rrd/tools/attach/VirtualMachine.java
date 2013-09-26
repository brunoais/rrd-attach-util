package com.rrd.tools.attach;

import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.Random;

/**
 * A class representing a remote Virtual Machine instance
 * 
 * @author erachitskiy
 * 
 */
public class VirtualMachine {
	private Object virtualMachineObject;

	/**
	 * Construct a virtual machine using underlying vm instance
	 * 
	 * @param virtualMachineObject
	 *            vm instance
	 */
	VirtualMachine(Object virtualMachineObject) {
		this.virtualMachineObject = virtualMachineObject;
	}

	/**
	 * Get this virtual machine's system properties
	 * 
	 * @return system properties
	 */
	public Properties getSystemProperties() {
		try {
			Method loadAgentMethod = virtualMachineObject.getClass().getMethod(
					"getSystemProperties");
			return (Properties) loadAgentMethod.invoke(virtualMachineObject);
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}

	/**
	 * Load a JVM agent on this machine
	 * 
	 * @param agentJar
	 *            path to agent JAR file
	 * @param agentOptions
	 *            options to supply to agent
	 */
	public void loadAgent(String agentJar, String agentOptions) {
		try {
			Method loadAgentMethod = virtualMachineObject.getClass().getMethod(
					"loadAgent", String.class, String.class);
			loadAgentMethod
					.invoke(virtualMachineObject, agentJar, agentOptions);
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}

	/**
	 * Detach from this virtual machine
	 */
	public void detach() {
		try {
			Method loadAgentMethod = virtualMachineObject.getClass().getMethod(
					"detach");
			loadAgentMethod.invoke(virtualMachineObject);
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}

	/**
	 * Attempt to find current virtual machine. Returns {@code null} if local VM
	 * can not be determined
	 * 
	 * @return local virtual machine instance or {@code null}
	 */
	public static VirtualMachine getThisVM() {
		/* set some system properties */
		String vmIdKey = VirtualMachine.class.getName() + "VMID";
		String vmIdValue = "vm"
				+ (System.currentTimeMillis() + new Random(
						System.currentTimeMillis()).nextInt());
		System.setProperty(vmIdKey, vmIdValue);
		for (VirtualMachineDescriptor virtualMachineDescriptor : list()) {
			try {
				VirtualMachine vm = virtualMachineDescriptor.attach();
				try {
					Properties vmProperties = vm.getSystemProperties();
					if (vmIdValue.equals(vmProperties.get(vmIdKey))) {
						return vm;
					}
				} catch (Exception e) {
					vm.detach();
				}
			} catch (Exception e) {
				/* swallow */				
			}
		}
		return null;
	}

	/**
	 * List all available virtual machines
	 * 
	 * @return list of virtual machine descriptors
	 */
	public static List<VirtualMachineDescriptor> list() {
		List<VirtualMachineDescriptor> ret = new LinkedList<VirtualMachineDescriptor>();
		try {
			Class<?> vmClass = Class
					.forName("com.sun.tools.attach.VirtualMachine");
			Method listMethod = vmClass.getDeclaredMethod("list");
			List<?> virtualMachines = (List<?>) listMethod.invoke(null);
			for (Object virtualMachineObject : virtualMachines) {
				VirtualMachineDescriptor virtualMachineDescriptor = new VirtualMachineDescriptor(
						(String) virtualMachineObject.getClass()
								.getMethod("id").invoke(virtualMachineObject),
						(String) virtualMachineObject.getClass()
								.getMethod("displayName")
								.invoke(virtualMachineObject));
				ret.add(virtualMachineDescriptor);
			}
		} catch (Exception e) {
			/* swallow */
		}
		return ret;
	}
}
