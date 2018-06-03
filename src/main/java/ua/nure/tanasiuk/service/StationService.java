package ua.nure.tanasiuk.service;

import org.springframework.stereotype.Service;
import ua.nure.tanasiuk.dao.StationDao;

import java.util.List;

@Service
public class StationService {
    private final StationDao stationDao;
    private final double[][] distanceMatrix;

    public StationService(StationDao stationDao) {
        this.stationDao = stationDao;

        distanceMatrix = stationDao.getDistanceMatrix();
    }

    public double[][] getDistanceMatrix(List<Integer> cities) {
        double[][] result = new double[cities.size()][];

        cities.forEach(c1 -> {
            result[c1] = new double[cities.size()];
            cities.forEach(c2 ->
                result[c1][c2] = distanceMatrix[c1][c2]
            );
        });

        return result;
    }
}
