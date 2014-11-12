package be.stehey.foxy.backtester.model;

public abstract class Event implements Cloneable {
	protected String type = "";

	@Override
	public Event clone() throws CloneNotSupportedException {
		return (Event)super.clone();
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

}
