package ua.nure.tanasiuk.algorithm;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Random;

@Service
@Slf4j
public class AntColonyAlgorithm {
    private Random random = new Random();
    public long seed = -1L;

//    old
//    private int alpha = 3;
//    private int beta = 2;
//    private double rho = 0.01;
//    private double Q = 2.0;

    private double alpha = 0.1;
    private double beta = 2.0;
    private double rho = 0.9;
    private double Q = 1.0;

    private int numAnts = 10;
    private int maxTime = 100000;

    public int[] makeRoute(double[][] distances) {
//        if (seed != -1L) {
//            random = new Random(seed);
//        }

        int numCities = distances.length;
        int[][] ants = new int[numAnts][];
        int[] bestRoute = new int[numCities];
        double bestLength = Double.MAX_VALUE;

        double[][] pheromones = initPheromones(numCities);

        for (int time = 0; time < maxTime; ++time) {
            updateAnts(ants, pheromones, distances);
            updatePheromones(pheromones, ants, distances);

            int[] currentBestRoute = bestRoute(ants, distances);
            double currentBestLength = length(currentBestRoute, distances);
            if (currentBestLength < bestLength) {
                bestLength = currentBestLength;
                bestRoute = currentBestRoute;
            }
        }

        return bestRoute;
    }

    private int indexOfTarget(int[] route, int target) {
        for (int i = 0; i < route.length; ++i) {
            if (route[i] == target) {
                return i;
            }
        }

        throw new RuntimeException();
    }

    private double length(int[] route, double[][] dists) {
        double result = 0.0;

        for (int i = 0; i < route.length - 1; ++i) {
            result += distanceBetweenCities(route[i], route[i + 1], dists);
        }
        result += distanceBetweenCities(route[0], route[route.length - 1], dists);

        return result;
    }

    private int[] bestRoute(int[][] ants, double[][] dists) {
        double bestLength = length(ants[0], dists);
        int idxBestLength = 0;

        for (int k = 1; k < ants.length; ++k) {
            double len = length(ants[k], dists);
            if (len < bestLength) {
                bestLength = len;
                idxBestLength = k;
            }
        }

        return Arrays.copyOf(ants[idxBestLength], ants[idxBestLength].length);
    }

    private double[][] initPheromones(int numCities) {
        double[][] pheromones = new double[numCities][];

        for (int i = 0; i < numCities; ++i) {
            pheromones[i] = new double[numCities];
        }

        for (int i = 0; i < pheromones.length; ++i) {
            for (int j = 0; j < pheromones[i].length; ++j) {
                pheromones[i][j] = 0.01;
            }
        }

        return pheromones;
    }

    private void updateAnts(int[][] ants, double[][] pheromones, double[][] dists) {
        int numCities = pheromones.length;

        for (int k = 0; k < ants.length; ++k) {
            int start = random.nextInt(numCities);
            int[] newRoute = buildRoute(k, start, pheromones, dists);
            ants[k] = newRoute;
        }
    }

    private int[] buildRoute(int k, int start, double[][] pheromones, double[][] dists) {
        int numCities = pheromones.length;
        int[] route = new int[numCities];
        boolean[] visited = new boolean[numCities];
        route[0] = start;
        visited[start] = true;

        for (int i = 0; i < numCities - 1; ++i) {
            int cityX = route[i];
            int next = nextCity(k, cityX, visited, pheromones, dists);
            route[i + 1] = next;
            visited[next] = true;
        }

        return route;
    }

    private int nextCity(int k, int cityX, boolean[] visited, double[][] pheromones, double[][] dists) {
        double[] probs = move(k, cityX, visited, pheromones, dists);

        double[] cumul = new double[probs.length + 1];
        for (int i = 0; i < probs.length; ++i) {
            cumul[i + 1] = cumul[i] + probs[i];
        }

        double p = random.nextDouble();

        for (int i = 0; i < cumul.length - 1; ++i) {
            if (p >= cumul[i] && p < cumul[i + 1]) {
                return i;
            }
        }

        throw new RuntimeException();
    }

    private double[] move(int k, int cityX, boolean[] visited, double[][] pheromones, double[][] dists) {
        int numCities = pheromones.length;
        double[] tau = new double[numCities];
        double sum = 0.0;

        for (int i = 0; i < tau.length; ++i) {
            if (i == cityX) {
                tau[i] = 0.0;
            } else if (visited[i]) {
                tau[i] = 0.0;
            } else {
                tau[i] = Math.pow(pheromones[cityX][i], alpha) * Math.pow((1.0 / distanceBetweenCities(cityX, i, dists)), beta);
                if (tau[i] < 0.0001) {
                    tau[i] = 0.0001;
                } else if (tau[i] > (Double.MAX_VALUE / (numCities * 100))) {
                    tau[i] = Double.MAX_VALUE / (numCities * 100);
                }
            }
            sum += tau[i];
        }

        double[] probs = new double[numCities];
        for (int i = 0; i < probs.length; ++i) {
            probs[i] = tau[i] / sum;
        }

        return probs;
    }

    private void updatePheromones(double[][] pheromones, int[][] ants, double[][] dists) {
        for (int i = 0; i < pheromones.length; ++i) {
            for (int j = i + 1; j < pheromones[i].length; ++j) {
                for (int k = 0; k < ants.length; ++k) {
                    double length = length(ants[k], dists);
                    double decrease = (1.0 - rho) * pheromones[i][j];
                    double increase = 0.0;

                    if (edgeInRoute(i, j, ants[k])) {
                        increase = (Q / length);
                    }

                    pheromones[i][j] = decrease + increase;

                    if (pheromones[i][j] < 0.0001) {
                        pheromones[i][j] = 0.0001;
                    } else if (pheromones[i][j] > 100000.0) {
                        pheromones[i][j] = 100000.0;
                    }

                    pheromones[j][i] = pheromones[i][j];
                }
            }
        }
    }

    private boolean edgeInRoute(int cityX, int cityY, int[] route) {
        int lastIndex = route.length - 1;
        int idx = indexOfTarget(route, cityX);

        if (idx == 0 && route[1] == cityY)
            return true;
        else if (idx == 0 && route[lastIndex] == cityY)
            return true;
        else if (idx == 0)
            return false;
        else if (idx == lastIndex && route[lastIndex - 1] == cityY)
            return true;
        else if (idx == lastIndex && route[0] == cityY)
            return true;
        else if (idx == lastIndex)
            return false;
        else if (route[idx - 1] == cityY)
            return true;
        else if (route[idx + 1] == cityY)
            return true;
        else
            return false;
    }

    private double distanceBetweenCities(int cityX, int cityY, double[][] dists) {
        return dists[cityX][cityY];
    }
}
