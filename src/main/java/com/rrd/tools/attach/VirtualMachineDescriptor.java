package com.rrd.tools.attach;

import java.lang.reflect.Method;

/**
 * A descriptor class for describing {@link VirtualMachine}s
 * 
 * @author erachitskiy
 * 
 */
public class VirtualMachineDescriptor {
	private String id;
	private String displayName;

	/**
	 * Constructor
	 * 
	 * @param id
	 *            vm id
	 * @param displayName
	 *            vm display name
	 */
	VirtualMachineDescriptor(String id, String displayName) {
		super();
		this.id = id;
		this.displayName = displayName;
	}

	/**
	 * Get the VM id
	 * 
	 * @return VM id
	 */
	public String getId() {
		return id;
	}

	/**
	 * Get the VM display name
	 * 
	 * @return VM display name
	 */
	public String getDisplayName() {
		return displayName;
	}

	/**
	 * Attach to this virtual machine
	 * 
	 * @return {@link VirtualMachine} instance
	 */
	public VirtualMachine attach() {
		try {
			Class<?> vmClass = Class
					.forName("com.sun.tools.attach.VirtualMachine");
			Method attachMethod = vmClass.getDeclaredMethod("attach",
					String.class);
			Object virtualMachine = attachMethod.invoke(null, getId());
			VirtualMachine vm = new VirtualMachine(virtualMachine);
			return vm;
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}

	public String toString() {
		return String.format("{id:%s,displayName:%s}", id, displayName);
	}
}
