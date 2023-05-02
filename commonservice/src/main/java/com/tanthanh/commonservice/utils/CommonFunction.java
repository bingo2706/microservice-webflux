package com.tanthanh.commonservice.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.networknt.schema.JsonSchema;
import com.networknt.schema.JsonSchemaFactory;
import com.networknt.schema.SpecVersion;
import com.networknt.schema.ValidationMessage;
import com.tanthanh.commonservice.common.ValidateException;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Slf4j
@NoArgsConstructor
public class CommonFunction {

    @SneakyThrows
    public static void jsonValidate(InputStream inputStream, String json)  {
        JsonSchema schema = JsonSchemaFactory.getInstance(SpecVersion.VersionFlag.V7).getSchema(inputStream);
        ObjectMapper om = new ObjectMapper();
        JsonNode jsonNode = om.readTree(json);
        Set<ValidationMessage> errors = schema.validate(jsonNode);
        Map<String,String> stringSetMap = new HashMap<>();
        for(ValidationMessage error: errors){
            if(stringSetMap.containsKey(formatStringValidate(error.getPath()))){
                String message = stringSetMap.get(formatStringValidate(error.getPath()));
                stringSetMap.put(formatStringValidate(error.getPath()),message + ", "+formatStringValidate(error.getMessage()));
            }else{
                stringSetMap.put(formatStringValidate(error.getPath()),formatStringValidate(error.getMessage()));
            }
        }
        if(!errors.isEmpty()){
            throw new ValidateException("RQ01",stringSetMap, HttpStatus.BAD_REQUEST);
        }
    }
    public static String formatStringValidate(String message){
        return message.replaceAll("\\$.","");
    }
}
