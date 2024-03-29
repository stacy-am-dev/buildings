package buildings;


import buildings.office.Office;
import buildings.patterns.BuildingFactory;
import buildings.patterns.DwellingFactory;

import java.io.*;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Comparator;
import java.util.Scanner;

public class Buildings {
    private static BuildingFactory buildingFactory = new DwellingFactory();


    public static void outputBuilding(Building building, OutputStream out) throws IOException {
        if (building != null) {
            DataOutputStream dout = new DataOutputStream(out);
            dout.writeInt(building.getFloorAmount());
            dout.writeChars(" ");
            for (int i = 0; i < building.getFloorAmount(); i++) {
                dout.writeInt(building.getFloor(i).getSpaceRoom());
                dout.writeChars(" ");
                for (int j = 0; j < building.getFloor(i).getSpaceRoom(); j++) {
                    dout.writeInt(building.getFloor(i).getSpace(j).getRoom());
                    dout.writeChars(" ");
                    dout.writeDouble(building.getFloor(i).getSpace(j).getArea());
                    dout.writeChars(" ");
                }
            }
        }
    }

    public static Building inputBuilding(InputStream in) throws IOException {
        DataInputStream din = new DataInputStream(in);
        Building result = null;
        if (din.available() > 0) {
            int floorsNum = din.readInt();
            din.skipBytes(2);
            Floor[] floors = new Floor[floorsNum];
            for (int i = 0; i < floorsNum; i++) {
                int floorRoomsCount = din.readInt();
                din.skipBytes(2);
                Space[] spaces = new Space[floorRoomsCount];
                for (int j = 0; j < floorRoomsCount; j++) {
                    int roomNum = din.readInt();
                    din.skipBytes(2);
                    double area = din.readDouble();
                    din.skipBytes(2);
                    Space space = createSpace(roomNum, area);
                    spaces[j] = space;
                }
                floors[i] = createFloor(spaces);
            }
            result = createBuilding(floors);
        }
        return result;
    }

    //use Reflation
    public static Building inputBuilding(InputStream in, Class buildingClass, Class floorClass, Class spaceClass) throws IOException {
        DataInputStream din = new DataInputStream(in);
        Building result = null;
        if (din.available() > 0) {
            int floorsNum = din.readInt();
            din.skipBytes(2);
            Floor[] floors = new Floor[floorsNum];
            for (int i = 0; i < floorsNum; i++) {
                int floorRoomsCount = din.readInt();
                din.skipBytes(2);
                Space[] spaces = new Space[floorRoomsCount];
                for (int j = 0; j < floorRoomsCount; j++) {
                    int roomNum = din.readInt();
                    din.skipBytes(2);
                    double area = din.readDouble();
                    din.skipBytes(2);
                    Space space = createSpace(roomNum, area, spaceClass);
                    spaces[j] = space;
                }
                floors[i] = createFloor(floorClass, spaces);
            }
            result = createBuilding(buildingClass, floors);
        }
        return result;
    }

    public static void writeBuilding(Building building, Writer out) throws IOException {
        if (building != null) {
            int floorsCount = building.getFloorAmount();
            out.write(Integer.toString(floorsCount));
            out.write(" ");
            for (int i = 0; i < building.getFloorAmount(); i++) {
                out.write(Integer.toString(building.getFloor(i).getSpaceAmount()));
                out.write(" ");
                for (int j = 0; j < building.getFloor(i).getSpaceAmount(); j++) {
                    out.write(Integer.toString(building.getFloor(i).getSpace(j).getRoom()));
                    out.write(" ");
                    out.write(Double.toString(building.getFloor(i).getSpace(j).getArea()));
                    out.write(" ");
                }
            }
        }
    }

    public static Building readBuilding(Reader in) throws IOException {
        BufferedReader bin = new BufferedReader(in);
        Building result = null;
        if (bin.ready()) {
            String[] s = bin.readLine().split(" ");
            int count = 0;
            int floorsNum = Integer.parseInt(s[count++]);
            Floor[] floors = new Floor[floorsNum];
            for (int i = 0; i < floorsNum; i++) {
                int floorRoomsCount = Integer.parseInt(s[count++]);
                Space[] spaces = new Space[floorRoomsCount];
                for (int j = 0; j < floorRoomsCount; j++) {
                    int roomNum = Integer.parseInt(s[count++]);
                    double area = Double.parseDouble(s[count++]);
                    Space space = createSpace(roomNum, area);
                    spaces[j] = space;
                }
                floors[i] = createFloor(spaces);
            }
            result = createBuilding(floors);
        }
        return result;
    }

    // use Reflation
    public static Building readBuilding(Reader in, Class buildingClass, Class floorClass, Class spaceClass) throws IOException {
        BufferedReader bin = new BufferedReader(in);
        Building result = null;
        if (bin.ready()) {
            String[] s = bin.readLine().split(" ");
            int count = 0;
            int floorsNum = Integer.parseInt(s[count++]);
            Floor[] floors = new Floor[floorsNum];
            for (int i = 0; i < floorsNum; i++) {
                int floorRoomsCount = Integer.parseInt(s[count++]);
                Space[] spaces = new Space[floorRoomsCount];
                for (int j = 0; j < floorRoomsCount; j++) {
                    int roomNum = Integer.parseInt(s[count++]);
                    double area = Double.parseDouble(s[count++]);
                    Space space = createSpace(roomNum, area, spaceClass);
                    spaces[j] = space;
                }
                floors[i] = createFloor(floorClass, spaces);
            }
            result = createBuilding(buildingClass, floors);
        }
        return result;
    }

    public static void serializeBuilding(Building building, OutputStream out) throws IOException {
        if (building != null) {
            ObjectOutputStream oos = new ObjectOutputStream(out);
            oos.writeObject(building);
        }
    }

    public static Building deserializeBuilding(InputStream in) throws IOException, ClassNotFoundException {
        Building result = null;
        ObjectInputStream ois = new ObjectInputStream(in);
        result = (Building) ois.readObject();
        return result;
    }

    public static void writeBuildingFormat(Building building, Writer out) throws IOException {
        if (building != null) {
            int floorsCount = building.getFloorAmount();
            out.write(String.format("Количество этажей: %d\n", floorsCount));
            for (int i = 0; i < building.getFloorAmount(); i++) {
                out.write(String.format("Количество помещений: %d\n", building.getFloor(i).getSpaceRoom()));
                for (int j = 0; j < building.getFloor(i).getSpaceRoom(); j++) {
                    out.write(String.format("Количество комнат в помещении: %d ", building.getFloor(i).getSpace(j).getRoom()));
                    out.write(String.format("Площадь помещения: %f\n", building.getFloor(i).getSpace(j).getArea()));
                }
            }
        }
    }

    public static Building readBuilding(Scanner scanner) {
        Building result = null;
        if (scanner.hasNext()) {
            String[] s = scanner.next().split(" ");
            int count = 0;
            int floorsNum = Integer.parseInt(s[count++]);
            Floor[] floors = new Floor[floorsNum];
            for (int i = 0; i < floorsNum; i++) {
                int floorRoomsCount = Integer.parseInt(s[count++]);
                Space[] offices = new Space[floorRoomsCount];
                for (int j = 0; j < floorRoomsCount; j++) {
                    int roomNum = Integer.parseInt(s[count++]);
                    double area = Double.parseDouble(s[count++]);
                    Space space = createSpace(roomNum, area);
                    offices[j] = space;
                }
                floors[i] = createFloor(offices);
            }
            result = createBuilding(floors);
        }
        return result;
    }

    // use Reflation
    public static Building readBuilding(Scanner scanner, Class buildingClass, Class floorClass, Class spaceClass) {
        Building result = null;
        if (scanner.hasNext()) {
            String[] s = scanner.next().split(" ");
            int count = 0;
            int floorsNum = Integer.parseInt(s[count++]);
            Floor[] floors = new Floor[floorsNum];
            for (int i = 0; i < floorsNum; i++) {
                int floorRoomsCount = Integer.parseInt(s[count++]);
                Space[] offices = new Space[floorRoomsCount];
                for (int j = 0; j < floorRoomsCount; j++) {
                    int roomNum = Integer.parseInt(s[count++]);
                    double area = Double.parseDouble(s[count++]);
                    Space space = createSpace(roomNum, area, spaceClass);
                    offices[j] = space;
                }
                floors[i] = createFloor(floorClass, offices);
            }
            result = createBuilding(buildingClass, floors);
        }
        return result;
    }

    public <T extends Comparable<T>> T[] sort(T[] object) {
        for (int i = 0; i < object.length; i++) {
            int minIndex = i;
            for (int j = i + 1; j < object.length; j++) {
                if (object[j].compareTo(object[minIndex]) < 0)
                    minIndex = j;
            }
            T swapBuf = object[i];
            object[i] = object[minIndex];
            object[minIndex] = swapBuf;
        }
        return object;
    }

    public static  <T> T[] sort(T[] object, Comparator<T> comparator) {
        for (int i = 0; i < object.length; i++) {
            int minIndex = i;
            for (int j = i + 1; j < object.length; j++) {
                if (comparator.compare(object[j], object[minIndex]) < 0)
                    minIndex = j;
            }
            T swapBuf = object[i];
            object[i] = object[minIndex];
            object[minIndex] = swapBuf;
        }
        return object;
    }

    // use Factory
    public static void setBuildingFactory(BuildingFactory buildingFactory) {
        Buildings.buildingFactory = buildingFactory;
    }

    public static Space createSpace(double area) {
        return buildingFactory.createSpace(area);
    }

    public static Space createSpace(int roomsCount, double area) {
        return buildingFactory.createSpace(roomsCount, area);
    }

    public static Floor createFloor(int spacesCount) {
        return buildingFactory.createFloor(spacesCount);
    }

    public static Floor createFloor(Space...spaces) {
        return buildingFactory.createFloor(spaces);
    }

    public static Building createBuilding(int floorsCount, int...spacesCounts) {
        return buildingFactory.createBuilding(floorsCount, spacesCounts);
    }

    public static Building createBuilding(Floor...floors) {
        return buildingFactory.createBuilding(floors);
    }

    public static Floor synchronizedFloor(Floor floor) {
        return new SynchronizedFloor(floor.getSpaceArray());
    }

    // use Reflection
    public static Space createSpace(double area, Class spaceClass) {
        try {
            Constructor<?> constructor = spaceClass.getConstructor(double.class);
            return (Space) constructor.newInstance(area);
        } catch (NoSuchMethodException | IllegalAccessException | InstantiationException | InvocationTargetException e) {
            throw new IllegalArgumentException();
        }
    }

    public static Space createSpace(int roomsCount, double area, Class spaceClass) {
        try {
            Constructor<?> constructor = spaceClass.getConstructor(double.class, int.class);
            return (Space) constructor.newInstance(area, roomsCount);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException | InstantiationException e) {
            throw new IllegalArgumentException();
        }
    }

    public static Floor createFloor(int spacesCount, Class floorClass) {
        try {
            Constructor<?> constructor = floorClass.getConstructor(int.class);
            return (Floor) constructor.newInstance(spacesCount);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException | InstantiationException e) {
            throw new IllegalArgumentException();
        }
    }

    public static Floor createFloor(Class floorClass, Space...spaces) {
        try {
            Constructor<?> constructor = floorClass.getConstructor(Space[].class);
            return (Floor) constructor.newInstance((Object) spaces);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException | InstantiationException e) {
            throw new IllegalArgumentException();
        }
    }

    public static Building createBuilding(int floorsCount, Class buildingClass, int...spacesCounts) {
        try {
            Constructor<?> constructor = buildingClass.getConstructor(int.class, int[].class);
            return (Building) constructor.newInstance(floorsCount, spacesCounts);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException | InstantiationException e) {
            throw new IllegalArgumentException();
        }
    }

    public static Building createBuilding(Class buildingClass, Floor...floors) {
        try {
            Constructor<?> constructor = buildingClass.getConstructor(Floor[].class);
            return (Building) constructor.newInstance((Object) floors);
        } catch (IllegalAccessException | InvocationTargetException | InstantiationException | NoSuchMethodException e) {
            throw new IllegalArgumentException();
        }
    }
}
