package com.wlai.bzl.service.amap.impl;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.wlai.bzl.service.amap.IAampService;
import org.locationtech.jts.geom.*;
import org.locationtech.proj4j.*;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class AmapServiceImpl implements IAampService {

    private static CRSFactory crsFactory = new CRSFactory();
    private static CoordinateReferenceSystem crsWGS84 = crsFactory.createFromName("EPSG:4326");
    private static CoordinateReferenceSystem crsUTM = crsFactory.createFromName("EPSG:32649"); // 中国49N
    private static CoordinateTransformFactory ctFactory = new CoordinateTransformFactory();
    private static CoordinateTransform transformWGS84ToUTM = ctFactory.createTransform(crsWGS84, crsUTM);

    @Override
    public double isPointInGeometries(double latitude, double longitude) {
        return 0;
    }

    @Override
    public double pointInGeometriesDistanceUtm(double latitude, double longitude) {
        InputStream inputStream = AmapServiceImpl.class.getResourceAsStream("/amap/china_margin_geo.json");
        StringBuilder jsonContent = new StringBuilder();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            String line;
            while ((line = reader.readLine()) != null){
                jsonContent.append(line);
            }

            JSONObject geoJsonObject = JSON.parseObject(jsonContent.toString());
            JSONArray features = geoJsonObject.getJSONArray("features");
            GeometryFactory geometryFactory = new GeometryFactory();
            List<Polygon> polygons = new ArrayList<>();
            for (int i = 0; i < features.size(); i++) {
                JSONObject feature = features.getJSONObject(i);
                JSONArray geometry = feature.getJSONObject("geometry").getJSONArray("coordinates");
                for (int h = 0; h < geometry.size(); h++) {
                    JSONArray coordinates = geometry.getJSONArray(h);
                    JSONArray outerCoordinates = coordinates.getJSONArray(0);
                    Coordinate[] coords = new Coordinate[outerCoordinates.size()];

                    for (int j = 0; j < outerCoordinates.size(); j++) {
                        JSONArray coord = outerCoordinates.getJSONArray(j);
                        Double x = coord.getDouble(0);
                        Double y = coord.getDouble(1);
                        ProjCoordinate srcCoord = new ProjCoordinate(x, y);
                        ProjCoordinate desCoord = new ProjCoordinate();
                        transformWGS84ToUTM.transform(srcCoord, desCoord);
                        coords[j] = new Coordinate(desCoord.x, desCoord.y);
                    }

                    LinearRing shell = geometryFactory.createLinearRing(coords);
                    Polygon polygon = geometryFactory.createPolygon(shell, null);
                    polygons.add(polygon);
                }
            }
            ProjCoordinate srcPoint = new ProjCoordinate(longitude, latitude);
            ProjCoordinate desPoint = new ProjCoordinate();
            transformWGS84ToUTM.transform(srcPoint, desPoint);
            Point point = geometryFactory.createPoint(new Coordinate(desPoint.x, desPoint.y));
            for (Polygon polygon : polygons) {
                if (polygon.contains(point)){
                    double distance = polygon.getBoundary().distance(point);
                    BigDecimal decimal = new BigDecimal(distance);
                    return decimal.doubleValue();
                }
            }
            return -1;
        }catch (Exception e){
            return -2;
        }
    }
}
