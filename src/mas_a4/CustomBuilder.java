package mas_a4;

import java.util.ArrayList;

import repast.simphony.context.Context;
import repast.simphony.context.space.continuous.ContinuousSpaceFactory;
import repast.simphony.context.space.continuous.ContinuousSpaceFactoryFinder;
import repast.simphony.context.space.grid.GridFactory;
import repast.simphony.context.space.grid.GridFactoryFinder;
import repast.simphony.dataLoader.ContextBuilder;
import repast.simphony.engine.environment.RunEnvironment;
import repast.simphony.parameter.Parameters;
import repast.simphony.space.continuous.ContinuousSpace;
import repast.simphony.space.continuous.SimpleCartesianAdder;
import repast.simphony.space.grid.Grid;
import repast.simphony.space.grid.GridBuilderParameters;
import repast.simphony.space.grid.SimpleGridAdder;
import repast.simphony.space.grid.StrictBorders;

public class CustomBuilder implements ContextBuilder<Object> {

	private ContinuousSpace<Object> space;
	private Grid<Object> grid;

	private ArrayList<Ziel> zielListe = new ArrayList<Ziel>();

	private Koordinator coordinator;
	
	@Override
	public Context build(Context<Object> context) {
		context.setId("mas_a4");

		// Space und Grid
		ContinuousSpaceFactory spaceFactory = ContinuousSpaceFactoryFinder.createContinuousSpaceFactory(null);
		space = spaceFactory.createContinuousSpace("space", context, new SimpleCartesianAdder<>(),
				new repast.simphony.space.continuous.StrictBorders(), 50, 50);

		GridFactory gridFactory = GridFactoryFinder.createGridFactory(null);

		grid = gridFactory.createGrid("grid", context,
				new GridBuilderParameters<Object>(new StrictBorders(), new SimpleGridAdder<Object>(), true, 50, 50));

		// Ziele
		erzeugeZiele(context, new Ziel(space, grid, 15, 10));
		erzeugeZiele(context, new Ziel(space, grid, 5, 35));
		erzeugeZiele(context, new Ziel(space, grid, 40, 10));
		erzeugeZiele(context, new Ziel(space, grid, 35, 45));
		erzeugeZiele(context, new Ziel(space, grid, 10, 35));

		// Verhandlung?
		Parameters params = RunEnvironment.getInstance().getParameters();
		System.out.println(params.getInteger("tage"));
		coordinator = new Koordinator(zielListe, params.getInteger("tage"));
		context.add(coordinator);

		// Boten
		erzeugeBote(context, new Bote(space, grid, 1), 5, 5);
		erzeugeBote(context, new Bote(space, grid, 2), 45, 5);
		erzeugeBote(context, new Bote(space, grid, 3), 5, 25);
		erzeugeBote(context, new Bote(space, grid, 4), 45, 45);

		return context;
	}

	private void erzeugeZiele(Context<Object> ctx, Ziel z) {
		ctx.add(z);
		space.moveTo(z, (int) z.getX(), (int) z.getY());
		grid.moveTo(z, (int) z.getX(), (int) z.getY());
		zielListe.add(z);
	}

	private void erzeugeBote(Context<Object> ctx, Bote b, int x, int y) {
		ctx.add(b);
		space.moveTo(b, (int) x, (int) y);
		grid.moveTo(b, (int) x, (int) y);
		
		b.setCoordinator(coordinator);
	}

}
