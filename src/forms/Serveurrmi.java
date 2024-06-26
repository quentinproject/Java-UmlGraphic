package forms;
import java.awt.geom.Area;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;

import forms.GeometricShapes.ComplexShape;
import forms.GeometricShapes.Shape;

public class Serveurrmi extends UnicastRemoteObject implements RemoteShapeService {
    private ArrayList<Shape> shapeList;
    private ArrayList<Shape> complexShapesData;
   
    public Serveurrmi() throws RemoteException {
        super();
        shapeList = new ArrayList<>();
        complexShapesData = new ArrayList<>();
    }

    public void saveShapes(List<Shape> shapes) throws RemoteException {
    	complexShapesData.clear();
        shapeList.clear();
        for (Shape shape : shapes) {
            if (shape instanceof ComplexShape) {
                //Reconstruction in App.java
                System.out.println("Found complex shape...");
                ComplexShape complexShape = (ComplexShape) shape;
                ArrayList<Shape> subShapes = complexShape.getShapes();           
                GeometricShapes.ComplexShape complexShape2 = new GeometricShapes().new ComplexShape((Area) complexShape.getShape(),subShapes, complexShape.getOperation());
                complexShapesData.add(complexShape2);
            } else shapeList.add(shape);
        }
        System.out.println("Shapes saved on server.");
    }


    public ArrayList<Shape> loadShapes() throws RemoteException {
        System.out.println("Shapes loaded from server.");
        ArrayList<Shape> shapes = new ArrayList<>();
        shapes.addAll(shapeList);
        
        for (Shape data : complexShapesData) {
            ComplexShape complexShape = (ComplexShape) data;
            ArrayList<Shape> subShapes = ((ComplexShape) data).getShapes();
            complexShape.setShape((Area) ( data.getShape()));
            complexShape.setShapes(subShapes);
            complexShape.setOperation(((ComplexShape) data).getOperation());;
            
            if (complexShape != null && subShapes != null) {
                shapes.add(complexShape);
            }
        }
        return shapes;
    }

    public static void main(String[] args) {
        try {
            RemoteShapeService server = new Serveurrmi();
            java.rmi.registry.LocateRegistry.createRegistry(1099);
            java.rmi.Naming.rebind("//localhost/RemoteShapeService", server);
            System.out.println("Server is ready...");
        } catch (Exception e) {
            System.err.println("Server exception: " + e.toString());
            e.printStackTrace();
        }
    }
}
