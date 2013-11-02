package com.ombillah.monitoring.domain;

import org.hyperic.sigar.ProcCpu;
import org.hyperic.sigar.Sigar;
import org.hyperic.sigar.SigarException;

public class SigarCpuLoadMonitor {

	private final Sigar sigar;
	private int cpuCount;
	private long pid;
	private ProcCpu prevPc;
	private double load;

	private static final int TOTAL_TIME_UPDATE_LIMIT = 2000;

	public void updateLoadTask() {
		try {
			ProcCpu curPc = sigar.getProcCpu(pid);
			long totalDelta = curPc.getTotal() - prevPc.getTotal();
			long timeDelta = curPc.getLastTime() - prevPc.getLastTime();
			if (totalDelta == 0) {
				if (timeDelta > TOTAL_TIME_UPDATE_LIMIT)
					load = 0;
				if (load == 0)
					prevPc = curPc;
			} else {
				load = 100. * totalDelta / timeDelta / cpuCount;
				prevPc = curPc;
			}
		} catch (SigarException ex) {
			throw new RuntimeException(ex);
		}
	}

	public SigarCpuLoadMonitor() throws SigarException {
		sigar = new Sigar();
		cpuCount = sigar.getCpuList().length;
		pid = sigar.getPid();
		prevPc = sigar.getProcCpu(pid);
		load = 0.;
	}
	
	public Long getLoad() {
		if(load < 0) {
			return 0L;
		}
		return Math.round(load);
	}
}