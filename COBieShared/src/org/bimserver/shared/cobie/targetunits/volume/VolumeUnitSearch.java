package org.bimserver.shared.cobie.targetunits.volume;


import org.bimserver.models.ifc2x3tc1.IfcUnitEnum;
import org.bimserver.shared.cobie.targetunits.IfcUnitSearch;

public class VolumeUnitSearch extends IfcUnitSearch
{

	public VolumeUnitSearch(String searchString)
	{
		super(searchString,IfcUnitEnum.VOLUMEUNIT);
	}

	@Override
	protected void addSearchItems()
	{
		add(new CubicCentimetersTextMatcher(searchString));
		add(new CubicFeetTextMatcher(searchString));
		add(new CubicInchesTextMatcher(searchString));
		add(new CubicKilometersTextMatcher(searchString));
		add(new CubicMetersTextMatcher(searchString));
		add(new CubicMillimetersTextMatcher(searchString));
		
	}

}