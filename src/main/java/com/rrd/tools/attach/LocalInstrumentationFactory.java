package com.rrd.tools.attach;

import java.io.File;
import java.io.FileOutputStream;
import java.lang.instrument.Instrumentation;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.commons.io.IOUtils;
/**
 * Factory for obtaining an {@link Instrumentation} instance of the local virtual machine
 * @author erachitskiy
 *
 */
public class LocalInstrumentationFactory {
	static Instrumentation instrumentation = null;
	/**
	 * Get the local instrumentation instance
	 * @return instrumentation instance
	 */
	public static Instrumentation getLocalInstrumentation() {
		if(instrumentation==null){
			/* attempt to load JVM agent */
			try{				
				File tmp = File.createTempFile("agent", ".jar");
				FileOutputStream fos = new FileOutputStream(tmp);
				ZipOutputStream zos = new ZipOutputStream(fos);
				zos.putNextEntry(new ZipEntry("META-INF"));
				zos.putNextEntry(new ZipEntry("META-INF/MANIFEST.MF"));
				String manifest = IOUtils.toString(LocalInstrumentationFactory.class.getResourceAsStream("manifest.mf.template"));
				manifest = manifest.replace("${agentClass}", LocalInstrumentationFactory.class.getName());
				IOUtils.write(manifest, zos);
				zos.close();
				fos.close();
				VirtualMachine.getThisVM().loadAgent(tmp.getAbsolutePath(), "");
			}catch(Exception e){
				throw new RuntimeException(e.getMessage(),e);
			}			
		}
		return instrumentation;
	}

	
    public static void premain(String args, Instrumentation inst) throws Exception {        
        instrumentation = inst;        
    }
	public static void agentmain(String args, Instrumentation inst)
			throws Exception {
		instrumentation = inst;		
	}
}
