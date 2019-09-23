/*
 * BSD 3-Clause License
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * * Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 *
 * * Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 *
 * * Neither the name of the copyright holder nor the names of its
 *   contributors may be used to endorse or promote products derived from
 *   this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 *
 * @author Mass++ Users Group (https://www.mspp.ninja/)
 * @author Satoshi Tanaka
 * @since 2019
 *
 * Copyright (c) Mon Sep 23 19:52:13 JST 2019 Satoshi Tanaka
 * All rights reserved.
 */
package ninja.mspp.model.entity;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import ninja.mspp.model.dataobject.Point;
import ninja.mspp.model.dataobject.XYData;


/**
 * The persistent class for the POINT_LISTS database table.
 *
 */
@Entity
@Table(name="POINT_LISTS")
@NamedQuery(name="PointList.findAll", query="SELECT p FROM PointList p")
public class PointList implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;

	@Column
	private Double maxX;

	@Column
	private Double maxY;

	@Column
	private Double minX;

	@Column
	private Double minY;

	@Column
	private Integer dataLength;

	@Lob
	@Column
	private byte[] xArray;

	@Lob
	@Column
	private byte[] yArray;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Double getMaxX() {
		return maxX;
	}

	public void setMaxX(Double maxX) {
		this.maxX = maxX;
	}

	public Double getMaxY() {
		return maxY;
	}

	public void setMaxY(Double maxY) {
		this.maxY = maxY;
	}

	public Double getMinX() {
		return minX;
	}

	public void setMinX(Double minX) {
		this.minX = minX;
	}

	public Double getMinY() {
		return minY;
	}

	public void setMinY(Double minY) {
		this.minY = minY;
	}

	public Integer getDataLength() {
		return dataLength;
	}

	public void setDataLength(Integer dataLength) {
		this.dataLength = dataLength;
	}

	public byte[] getxArray() {
		return xArray;
	}

	public void setxArray(byte[] xArray) {
		this.xArray = xArray;
	}

	public byte[] getyArray() {
		return yArray;
	}

	public void setyArray(byte[] yArray) {
		this.yArray = yArray;
	}

	/**
	 * gets the xy data;
	 * @return xy data
	 */
	public XYData getXYData() {
		DataInputStream xIn = new DataInputStream( new ByteArrayInputStream( this.xArray ) );
		DataInputStream yIn = new DataInputStream( new ByteArrayInputStream( this.yArray ) );

		List< Point< Double > > points = new ArrayList< Point< Double > >();

		try {
			for( int i = 0; i < this.dataLength; i++ ) {
				double x = xIn.readDouble();
				double y = yIn.readDouble();
				Point< Double > point = new Point< Double >( x, y );
				points.add( point );
			}
			xIn.close();
			yIn.close();
		}
		catch( Exception e ) {
			e.printStackTrace();
			return null;
		}

		XYData xyData = new XYData( points, false );
		if( this.minX != null ) {
			xyData.setMinX( this.minX );
		}
		if( this.maxX != null ) {
			xyData.setMaxX( this.maxX );
		}
		if( this.minY != null ) {
			xyData.setMinY( this.minY );
		}
		if( this.maxY != null ) {
			xyData.setMaxY( this.maxY );
		}

		return xyData;
	}
}
