package com.dixon.game.ddz.common.enu;
/**
 * 1	一个
 * 2	一对
 * 3	三个
 * 4	炸弹
 * 11	王炸
 * 31	三带一
 * 32	三带一对
 * 411	四带两
 * 422	四带两对
 * 111111(>=5)	顺子
 * 222(>=3)连队
 * 33	飞机不带
 * 3311	飞机带两个
 * 3322	飞机带两队
 * ...
 * 1=a
 * 2=b
 * 3=c
 * 4=d
 */
public enum Type {
	a("OneExecutor"),
	b("PairExecutor"),
	c("ThreeExecutor"),
	d("BombExecutor"),
	aa("BombExecutor"),
	ca("ThreeExecutor"),
	cb("ThreeExecutor"),
	daa("FourExecutor"),
	db("FourExecutor"),
	dbb("FourExecutor"),
	aaaaa("RowExecutor"),
	bbb("MultiPairExecutor"),
	cc("PlaneExecutor"),
	ccaa("PlaneExecutor"),
	ccb("PlaneExecutor"),
	ccbb("PlaneExecutor"),
	ccc("PlaneExecutor"),
	cccaaa("PlaneExecutor"),
	cccba("PlaneExecutor"),
	cccbbb("PlaneExecutor"),
	cccc("PlaneExecutor"),
	ccccaaaa("PlaneExecutor"),
	ccccbb("PlaneExecutor"),
	ccccbaa("PlaneExecutor"),
	ccccc("PlaneExecutor"),
	cccccaaaaa("PlaneExecutor");
	
	private String name;
	Type(String name){
		this.name = name;
	}
	public String getName(){
		return name;
	}
	
	public static void main(String[] args) {
		System.out.println(Type.valueOf("a").getName());
	}
}
