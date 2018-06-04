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

        for (int i = 0; i < cities.size(); i++) {
            result[i] = new double[cities.size()];
            for (int j = 0; j < cities.size(); j++) {
                result[i][j] = distanceMatrix[cities.get(i) - 1][cities.get(j) - 1];
            }
        }

        return result;
    }
}
