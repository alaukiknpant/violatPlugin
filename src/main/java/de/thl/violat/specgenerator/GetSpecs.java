package de.thl.violat.specgenerator;

import com.github.cliftonlabs.json_simple.JsonObject;

import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingDeque;

public class GetSpecs {

    public GetSpecs() {
    }

    public static JsonObject getSpecs(String methodName) throws ClassNotFoundException {
        Class cls = Class.forName(methodName);
//        Package pack = cls.getPackage();
//        String packageName = pack.getName();
        String className = cls.getName();
//        className = packageName + "." + className;
        Method[] methods = cls.getMethods();
        ArrayList<Method> methods1 = new ArrayList<Method>();
        int idx = 0;
        while (idx < methods.length && !methods[idx].getName().equals("wait")) {
            methods1.add(methods[idx]);
            idx++;
        }

        methods = methods1.toArray(new Method[0]);

        Methoed[] methoeds = new Methoed[methods.length];
//        System.out.println(methods.length);

        for (int i = 0; i < methods.length; i++) {
//            System.out.println(i);
            String method_name = methods[i].getName();
            System.out.println(method_name);

            Class[] parameters = methods[i].getParameterTypes();
            ArrayList<String> pars = new ArrayList<String>(parameters.length);
            for (int q = 0; q < parameters.length; q++) {
                Class parameterType = parameters[q];
//                System.out.println(parameterType.getName());
                String parameter_name = parameterType.getName();
                pars.add(parameter_name);
            }
            System.out.println("\n");
            boolean isReturnVoid = methods[i].getReturnType().equals(Void.TYPE);



            Methoed m = new Methoed(method_name, pars, isReturnVoid, true, true, "complete");
            methoeds[i] = m;
        }

        ClassJSON c = new ClassJSON(className, 3, methoeds);

        return c.classToJSON();

    }

//    public static void main(String args[]) throws ClassNotFoundException {
//        Class cls = Class.forName("java.util.concurrent.LinkedBlockingDeque");
//        String className = cls.getName();
//        System.out.println(className);

//
//        String s = "a,b,c,d,e,.........";
//        List<String> arr = Arrays.asList(s.split(","));
//        System.out.println(arr.toString());
//        System.out.println(arr.get(0));
//
//
//        JsonObject j = getSpecs("com.foo.QueueSynchronized");
//        System.out.println(j.toJson());
//
//
//        try (FileWriter file = new FileWriter("QueueSynchronized.json")) {
//            file.write(j.toJson());
//            file.flush();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
}

