package org.immregistries.vaccination_deduplication.utils;

import org.immregistries.dqa.codebase.client.CodeMap;
import org.immregistries.dqa.codebase.client.CodeMapBuilder;
import org.immregistries.dqa.codebase.client.RelatedCode;
import org.immregistries.dqa.codebase.client.generated.Code;
import org.immregistries.vaccination_deduplication.Immunization;
import org.immregistries.vaccination_deduplication.LinkedImmunization;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class ImmunizationNormalisation {
    private static ImmunizationNormalisation instance;

    CodeMapBuilder codeMapBuilder = CodeMapBuilder.INSTANCE;
    InputStream inputStream;
    CodeMap codeMap;
    RelatedCode relatedCode;
    DateFormat dateFormat = new SimpleDateFormat("mm/dd/yyyy");

    private ImmunizationNormalisation() {}

    public void initialize() {
        inputStream = CodeMapBuilder.class.getResourceAsStream("/DQA_CM_2.1.xml");
        codeMap = codeMapBuilder.getCodeMap(inputStream);
        relatedCode = new RelatedCode(codeMap);
    }

    public void initialize(String codebaseFilePath) throws FileNotFoundException {
        File file = new File(codebaseFilePath);
        inputStream = new FileInputStream(file);
        codeMap = CodeMapBuilder.INSTANCE.getCodeMap(inputStream);
        relatedCode = new RelatedCode(codeMap);
    }

    public void refreshCodebase(String codebaseFilePath) throws FileNotFoundException {
        File file = new File(codebaseFilePath);
        inputStream = new FileInputStream(file);
        codeMap = CodeMapBuilder.INSTANCE.getCodeMap(inputStream);
        relatedCode = new RelatedCode(codeMap);
    }

    public static ImmunizationNormalisation getInstance() {
        if (ImmunizationNormalisation.instance == null) {
            ImmunizationNormalisation.instance = new ImmunizationNormalisation();
        }
        return ImmunizationNormalisation.instance;
    }

    public void normalizeImmunization(Immunization immunization){
        immunization.setVaccineGroupList(new ArrayList<String>(relatedCode.getVaccineGroupLabelsFromCvx(immunization.getCVX())) );

        Code productCode = codeMap.getProductFor(immunization.getCVX(), immunization.getMVX(), dateFormat.format(immunization.getDate()));

        if (productCode != null) {
            immunization.setProductCode(productCode.getValue());
        }

    }

    public void normalizeAllImmunizations(LinkedImmunization linkedImmunization){
        for (Immunization immunization : linkedImmunization) {
            normalizeImmunization(immunization);
        }
    }
}
