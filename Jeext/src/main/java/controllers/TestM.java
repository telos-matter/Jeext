package controllers;

import jeext.controller.core.param.annotations.MID;
import jeext.model.Model;

public class TestM extends Model<TestM> {

	@MID
	private Integer id;
	
	@Override
	public Object getId() {
		return null;
	}

}
