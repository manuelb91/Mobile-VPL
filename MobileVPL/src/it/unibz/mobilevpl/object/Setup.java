package it.unibz.mobilevpl.object;

import java.util.List;

import com.orm.SugarRecord;

public class Setup extends SugarRecord<Setup> {
	
	private boolean executeLocal;
	private String remoteAddress;
	
	
	public Setup() {
		this.executeLocal = true;
		this.remoteAddress = "";
	}
	
	public static Setup getSetup() {
		List<Setup> setupList = Setup.listAll(Setup.class);
		if(setupList.size() > 0)
			return setupList.get(0);
		Setup setup = new Setup();
		long id = setup.save();
		return Setup.findById(Setup.class, id);
	}

	public boolean isExecuteLocal() {
		return executeLocal;
	}

	public void setExecuteLocal(boolean executeLocal) {
		this.executeLocal = executeLocal;
	}

	public String getRemoteAddress() {
		return remoteAddress;
	}

	public void setRemoteAddress(String remoteAddress) {
		this.remoteAddress = remoteAddress;
	}
	
}
