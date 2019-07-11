package ninja.mspp.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.querydsl.core.types.dsl.BooleanExpression;

import ninja.mspp.model.dataobject.DrawPoint;
import ninja.mspp.model.dataobject.FastDrawData;
import ninja.mspp.model.dataobject.Point;
import ninja.mspp.model.dataobject.XYData;
import ninja.mspp.model.entity.DrawElementList;
import ninja.mspp.model.entity.PointList;
import ninja.mspp.model.entity.QDrawElementList;
import ninja.mspp.repository.DrawElementListRepository;
import ninja.mspp.repository.PointListRepository;

@Service
@Transactional
public class PointsService {
	/** min range */
	public static Double MIN_RANGE = 0.01;

	/** max level */
	public static Integer MAX_LEVEL = 10;


	@Autowired
	private PointListRepository pointListRepository;

	@Autowired
	private DrawElementListRepository elementListRepository;

	/**
	 * saves points
	 * @param points points
	 * @return point list object
	 */
	public PointList savePoints( List< Point< Double > > points, String xUnit, String yUnit ) {
		PointList list = new PointList();

		XYData xyData = new XYData( points );

		Double minX = xyData.getMinX();
		Double maxX = xyData.getMaxX();
		Double minY = xyData.getMinY();
		Double maxY = xyData.getMaxY();

		list.setMinX( minX );
		list.setMaxX( maxX );
		list.setMinY( minY );
		list.setMaxY( maxY );
		list.setXunit( xUnit );
		list.setYunit( yUnit );
		list.setXYData( xyData );

		list = this.pointListRepository.save( list );

		this.saveDrawElementLists( list, points );

		return list;
	}

	/**
	 * save draw element lists
	 * @param list point list
	 * @param points points
	 */
	private void saveDrawElementLists( PointList list, List< Point< Double> > points ) {
		double range = PointsService.MIN_RANGE;
		for( int level = 1; level <= PointsService.MAX_LEVEL; level++ ) {
			this.saveDrawElementList( list, points, range, level );
			range = range * 2.0;
		}
	}

	/**
	 * saves draw element list
	 * @param list
	 * @param points
	 * @param range
	 * @param level
	 */
	private void saveDrawElementList( PointList list, List< Point< Double > > points, double range, int level ) {
		DrawElementList elementList = new DrawElementList();
		elementList.setPointListId( list.getId() );
		elementList.setLevel( level );

		List< DrawPoint > elements = new ArrayList< DrawPoint >();
		int currentIndex = -1;
		DrawPoint currentElement = null;
		for( Point< Double > point : points ) {
			double x = point.getX();
			double y = point.getY();
			int index = ( int )Math.round( x / range );

			if( index > currentIndex || currentElement == null ) {
				DrawPoint element = new DrawPoint();
				element.setX( range * ( double )index );
				element.setMaxY( y );
				element.setMinY( y );
				element.setLeftY( y );
				element.setRightY( y );
				elements.add( element );

				currentIndex = index;
				currentElement = element;
			}
			else {
				currentElement.setRightY( y );
				if( y < currentElement.getMinY() ) {
					currentElement.setMinY( y );
				}
				if( y > currentElement.getMaxY() ) {
					currentElement.setMaxY( y );
				}
			}
		}

		elementList.setDrawPoints( elements );
		elementList = this.elementListRepository.save( elementList );
	}

	/**
	 * finds points
	 * @param id points id
	 * @return points
	 */
	public PointList findPointList( int id ) {
		PointList points = null;
		Optional< PointList > optional = this.pointListRepository.findById( id );
		if( optional.isPresent() ) {
			points = optional.get();
		}
		return points;
	}

	/**
	 * gets  fast draw data
	 * @param pointList point list
	 * @return fast draw data
	 */
	public FastDrawData getFastDrawData( PointList pointList ) {
		int pointId = pointList.getId();

		QDrawElementList qPoints = QDrawElementList.drawElementList;
		BooleanExpression expression = qPoints.pointListId.eq( pointId );
		List< DrawElementList > lists = new ArrayList< DrawElementList >();
		for( DrawElementList elements : elementListRepository.findAll( expression ) ) {
			lists.add( elements );
		}
		lists.sort(
			( list1, list2 ) -> {
				return ( list1.getLevel() - list2.getLevel() );
			}
		);
		return new FastDrawData( pointList, lists );
	}

}
