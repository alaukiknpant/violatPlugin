package plugin.linearizability.violat.specgenerator;

import com.github.cliftonlabs.json_simple.JsonObject;

public class ClassJSON {
    public String class_name;
    public Integer invocations;
    public Methoed[] methoeds;

    public ClassJSON(String class_name, Integer invocations, Methoed[] methoeds) {
        this.class_name = class_name;
        this.invocations = invocations;
        this.methoeds = methoeds;
    }

    public JsonObject classToJSON(){
        JsonObject classJSON = new JsonObject();
        JsonObject invocations = new JsonObject();
        JsonObject[] json_methods = new JsonObject[this.methoeds.length];
        for (int i = 0; i < this.methoeds.length; i++){
            json_methods[i] = this.methoeds[i].convertToJson();
        }
        invocations.put("invocations", this.invocations);

        classJSON.put("class", this.class_name);
        classJSON.put("harnessParameters", invocations);
        classJSON.put("methods", json_methods);
        return classJSON;
    }

//    public static void main(String args[]) {
//        ArrayList<String> kado = new ArrayList<String>(3);
//        kado.add("java.lang.Object");
//        kado.add("java.lang.Object");
//        kado.add("java.lang.Object");
//
//        Methoed m0 = new Methoed("lado",kado, true, true, true);
//        Methoed m1 = new Methoed("puti",kado, true, true, true);
//        Methoed m2 = new Methoed("chikne", kado, true, true, true);
//        Methoed[] methoeds = {m0, m1, m2};
//        ClassJSON c = new ClassJSON("lado", 3, methoeds);
//        System.out.println(c.classToJSON().toJson());
//
//
//    }



}
