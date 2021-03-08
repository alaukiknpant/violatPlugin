package plugin.linearizability.violat.specgenerator;

import com.github.cliftonlabs.json_simple.JsonObject;

import java.util.ArrayList;

public class Methoed {
    public String name;
    public ArrayList<String> parameters;
    public boolean is_void;
    public boolean is_readonly;
    public boolean is_trusted;
    public String visibility;

    public Methoed(String name, ArrayList<String> parameters, boolean is_void, boolean is_readonly,
                   boolean is_trusted, String visibility) {
        this.name = name;
        this.parameters = parameters;
        this.is_void = is_void;
        this.is_readonly = is_readonly;
        this.is_trusted = is_trusted;
        this.visibility = visibility;
    }

    public JsonObject convertToJson() {
        JsonObject method = new JsonObject();
        JsonObject[] parameters = new JsonObject[this.parameters.size()];
        for (int i= 0; i < this.parameters.size(); i++) {
            JsonObject parameter = new JsonObject();
            parameter.put("type", this.parameters.get(i));
            parameters[i] = parameter;
        }

        method.put("name", this.name);
        method.put("parameters", parameters);
        method.put("trusted", this.is_trusted);
        method.put("void", this.is_void);
        method.put("readonly", this.is_readonly);
        method.put("visibility", this.visibility);

        return method;
    }

//    public static void main(String[] args)  {
//        ArrayList<String> kado = new ArrayList<String>(3);
//        kado.add("java.lang.Object");
//        kado.add("java.lang.Object");
//        kado.add("java.lang.Object");
//
//        Methoed m = new Methoed("lado", kado, true, true, true);
//        JsonObject j = m.convertToJson();
//        System.out.println(j.toJson());
//    }

}




