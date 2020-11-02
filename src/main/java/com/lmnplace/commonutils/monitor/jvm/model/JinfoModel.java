package com.lmnplace.commonutils.monitor.jvm.model;
import java.util.List;
public class JinfoModel {
	private List<String> noedefault;
    private List<String> commandLine;
    public JinfoModel(List<String> noedefault, List<String> commandLine) {
        this.noedefault = noedefault;
        this.commandLine = commandLine;
    }
	public List<String> getNoedefault() {
		return noedefault;
	}
	public void setNoedefault(List<String> noedefault) {
		this.noedefault = noedefault;
	}
	public List<String> getCommandLine() {
		return commandLine;
	}
	public void setCommandLine(List<String> commandLine) {
		this.commandLine = commandLine;
	}
	
}
