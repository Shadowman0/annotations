package ${packageName};
 
import ${sourcePackageName}.${source};
import ${targetPackageName}.${target};
 
public class ${generatedClazzName} {

public static ${target} map(${source} from){
	${target} result = new ${target}();
 	<#list fields as field>
		result.set${field.name}(from.get${field.name}());
   	</#list >
   	return result;
   	}

}