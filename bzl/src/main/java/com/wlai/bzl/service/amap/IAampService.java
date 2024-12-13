package com.wlai.bzl.service.amap;

public interface IAampService {

    double isPointInGeometries(double latitude, double longitude);

    double pointInGeometriesDistanceUtm(double latitude, double longitude);
}
