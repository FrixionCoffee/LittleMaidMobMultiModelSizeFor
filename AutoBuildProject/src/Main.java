import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class Main {

    public static void main(String[] args) {
        var s = GradleFunction.getSourceFileName();
        System.out.println(s);

        GradleFunction.getBuildList()
                .forEach(System.out::println);
    }

}

class GradleFunction {

    /**
     * java直下にあるファイルで、ファイル名にModelLittleMaidが含まれるファイルの最も最初にマッチした物を返す
     * 基本的にModelLittleMaidを含むファイル名は一つしかないと仮定している
     *
     * Mint OSだと相対パスで記述できたけど、WindowsだとGradleがコケるのでやむを得ず絶対パス表記にした。
     * もし、このコードを使用される方が居たら、適切なパスに変更してください。
     */
    static String getSourceFileName() {

        var workDir = Paths.get("D:\\Minecraft_Modding\\LittleMaidMobMultiModelSizeFor\\src\\main\\java");

        String sourceFileCodeName = "Error";
        try {
            sourceFileCodeName = Files.list(workDir).
                    findFirst()
                    .orElseThrow(IllegalStateException::new)
                    .getFileName()
                    .toString();
        } catch (IOException e) {
            e.printStackTrace();
        }


//        for (Path path: Files.list(workDir)){
//            String s = path.getFileName().toString();
//            if (s.contains("ModelLittleMaid")) {
//                sourceFileCodeName = s;
//                break;
//            }
//        }
        String sourceFileStrPath = "D:\\Minecraft_Modding\\LittleMaidMobMultiModelSizeFor\\src\\main\\java" + sourceFileCodeName;
        Path sourceFilePath = Paths.get(sourceFileStrPath);
        return sourceFilePath.getFileName().toString();
    }

    static String getBuildName(String fullSourceFileName) {
        final int endIndex = fullSourceFileName.indexOf(".");
        final int startIndex = fullSourceFileName.indexOf("_") + 1;
        return fullSourceFileName.substring(startIndex, endIndex);
    }

    public static class BuildName {
        final String CLASS_NAME;
        final String CLASS_FILE_NAME;
        final String SIZE_RATE_NAME;

        BuildName(String baseName, String sizeRateName) {
            CLASS_NAME = createClassName(baseName);

            String suffix = "";
            if (!CLASS_NAME.contains(".java")){
                suffix = ".java";
            }
            CLASS_FILE_NAME = CLASS_NAME + suffix;
            SIZE_RATE_NAME = "SizeRate." + sizeRateName;
        }

        /**
         * @param baseName example: "333DangerNotSR2"
         * @return example: ""ModelLittleMaid_ZeroDot333DangerNotSR2
         */
        private String createClassName(final String baseName) {
            return "ModelLittleMaid_ZeroDot" +
                    baseName;
        }

        @Override
        public String toString() {
            return "BuildName{" +
                    "CLASS_NAME='" + CLASS_NAME + '\'' +
                    ", CLASS_FILE_NAME='" + CLASS_FILE_NAME + '\'' +
                    ", SIZE_RATE_NAME='" + SIZE_RATE_NAME + '\'' +
                    '}';
        }
    }

    static boolean isNotSR2() {
        return true;
    }

    static List<String> getBaseClassNameList() {
        final List<String> baseNames = new ArrayList<>();
        baseNames.add("200Danger");
        baseNames.add("250Danger");
        baseNames.add("300Danger");
        baseNames.add("350Danger");
        baseNames.add("400Danger");
        baseNames.add("450");
        baseNames.add("500");
        baseNames.add("550");
        baseNames.add("600");
        baseNames.add("650");
        baseNames.add("700");
        baseNames.add("750");
        baseNames.add("800");
        baseNames.add("850");
        baseNames.add("900");
        baseNames.add("950");
        baseNames.add("370PassedHalfBlockDanger");
        baseNames.add("733PassedBlock");
        baseNames.add("333Danger");

        if (baseNames.size() != 19) {
            throw new AssertionError();
        }
        return baseNames;
    }

    static List<String> getBaseNameListOrAddNotSR2() {
        if (!isNotSR2()) {
            return getBaseClassNameList();
        }

        return changeNotSR2(getBaseClassNameList());
    }

    static List<String> changeNotSR2(final List<String> baseNameList) {
        List<String> baseNamesAddNotSR2 = new ArrayList<>();
        for (var baseName : baseNameList) {
            baseNamesAddNotSR2.add(baseName + "NotSR2");
        }
        return baseNamesAddNotSR2;
    }

    static Map<String, String> getSizeRateNameMap() {
        final NavigableMap<String, String> sizeRateNameMap = new TreeMap<>();
        sizeRateNameMap.put("200Danger", "ZERO_DOT200_DANGER");
        sizeRateNameMap.put("250Danger", "ZERO_DOT250_DANGER");
        sizeRateNameMap.put("300Danger", "ZERO_DOT300_DANGER");
        sizeRateNameMap.put("333Danger", "ZERO_DOT333_DANGER");
        sizeRateNameMap.put("350Danger", "ZERO_DOT350_DANGER");
        sizeRateNameMap.put("400Danger", "ZERO_DOT400_DANGER");
        sizeRateNameMap.put("450", "ZERO_DOT450");
        sizeRateNameMap.put("500", "ZERO_DOT500");
        sizeRateNameMap.put("550", "ZERO_DOT550");
        sizeRateNameMap.put("600", "ZERO_DOT600");
        sizeRateNameMap.put("650", "ZERO_DOT650");
        sizeRateNameMap.put("700", "ZERO_DOT700");
        sizeRateNameMap.put("750", "ZERO_DOT750");
        sizeRateNameMap.put("800", "ZERO_DOT800");
        sizeRateNameMap.put("850", "ZERO_DOT850");
        sizeRateNameMap.put("900", "ZERO_DOT900");
        sizeRateNameMap.put("950", "ZERO_DOT950");
        sizeRateNameMap.put("370PassedHalfBlockDanger", "PASSED_HALF_BLOCK_DANGER");
        sizeRateNameMap.put("733PassedBlock", "PASSED_BLOCK");

        if (sizeRateNameMap.size() != 19) {
            throw new AssertionError();
        }

        final var addNotSR2List = changeNotSR2(getBaseClassNameList());

        for (var i: addNotSR2List) {
            final var rowKey = sizeRateNameMap.floorKey(i);
            final var sizeRateName = sizeRateNameMap.get(rowKey);
            sizeRateNameMap.put(i, sizeRateName);
        }

        return sizeRateNameMap;
    }

    static List<BuildName> getBuildList() {
        final List<BuildName> buildList = new ArrayList<>();
        final var sizeRateMap = getSizeRateNameMap();
        final var classBaseNameList = getBaseNameListOrAddNotSR2();
        for (var baseName: classBaseNameList) {
            buildList.add(new BuildName(baseName, sizeRateMap.get(baseName)));
        }

        return buildList;
    }

    static String getProjectDirName() {
        return "D:\\Minecraft_Modding\\LittleMaidMobMultiModelSizeFor";
    }

    static Path getProjectJavaDirPath() {
        return Paths.get(getProjectDirName(), "src\\main\\java");
    }

    static Path getNowJavaFilePath() {
        Path path = getProjectJavaDirPath();
        try {
            final var pathStream = Files.list(path);
            return pathStream.findFirst().orElseThrow(IllegalStateException::new);
        } catch (IOException e) {
            e.printStackTrace();
        }
        throw new AssertionError();
    }


    static void translateResourcesDirName() {
        Path resourcePath = null;
//        for (Path path : Files.list(Paths.get("D:\\Minecraft_Modding\\LittleMaidMobMultiModelSizeFor\\src\\main\\resources\\assets\\minecraft\\textures\\entity\\littleMaid"))) {
//            resourcePath = path;
//            break;
//        }
        try {
            resourcePath = Files.list(Paths.get("D:\\Minecraft_Modding\\LittleMaidMobMultiModelSizeFor\\src\\main\\resources\\assets\\minecraft\\textures\\entity\\littleMaid"))
                    .findFirst()
                    .orElseThrow(IllegalStateException::new);
        } catch (IOException e) {
            e.printStackTrace();
        }

        final String baseName = "SizeFor_" + getBuildName(getSourceFileName());
        Objects.requireNonNull(resourcePath);
        final Path newResourcePath = resourcePath.resolveSibling(Paths.get(baseName));
        try {
            Files.move(resourcePath, newResourcePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static List<String> getJavaCodeAllLines() {
        try {
            return Files.readAllLines(getNowJavaFilePath(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
        }
        throw new IllegalStateException();
    }

    static List<String> changeJavaCode(String classBaseName, String sizeRateName) {
        var javaCode = getJavaCodeAllLines();

        List<String> overWrittenJavaCode = new ArrayList<>();
        for (var line : javaCode) {
            if (line.contains("private static final SizeRate sizeRate = ")) {
                overWrittenJavaCode.add("private static final SizeRate sizeRate = " + sizeRateName + ";");
                continue;
            }

            if (line.contains("public ModelLittleMaid_ZeroDot")) {
                String text = classBaseName + line.substring(line.indexOf("("));
                overWrittenJavaCode.add(text);
                continue;
            }

            if (line.contains("public class ModelLittleMaid_ZeroDot")) {
                overWrittenJavaCode.add("public class " + classBaseName + " extends ModelLittleMaidBase {");
                continue;
            }
            overWrittenJavaCode.add(line);
        }
        return overWrittenJavaCode;

    }

    static List<String> getNotEqualsLineList(List<String> baseList, List<String> changedList) {
        if (baseList.size() != changedList.size()) {
            throw new IllegalArgumentException();
        }

        final List<String> resultList = new ArrayList<>();
        for (int i = 0; i < baseList.size(); i++) {
            if (!baseList.get(i).equals(changedList.get(i))) {
                resultList.add(changedList.get(i));
            }
        }

        return resultList;
    }


    static void listPrintln(List<Object> list) {
        for (var i : list) {
            System.out.println(i);
        }
    }

//    task example() {
//        doLast {
//            // doLastに入れないと同期のたびに実行されて面倒なことになる
//        }
//    }

//    task testChangeJavaCode() {
//        doLast {
//            final var rawText = getJavaCodeAllLines()
//            List<String> list = new ArrayList<>()
//            for (i in getBuildList()) {
//                final List<String> changedText = changeJavaCode(i.CLASS_NAME, i.SIZE_RATE_NAME)
//                list.addAll(getNotEqualsLineList(rawText, changedText))
//            }
//
//            listPrintln(list)
//
//
//        }
//    }

//    static void translateJavaFile(final BuildName buildName) {
//        var textList = changeJavaCode(buildName.CLASS_NAME, buildName.SIZE_RATE_NAME)
//        final Path oldJavaFilePath = getNowJavaFilePath()
//
//        var path = getProjectJavaDirPath().resolve(buildName.CLASS_FILE_NAME)
//        if (!Files.exists(path)) {
//            Files.createFile(path)
//        }
//        Files.write(path, textList, StandardCharsets.UTF_8, StandardOpenOption.WRITE)
//
//        Files.delete(oldJavaFilePath)
//    }

//    task testCreateJavaFile() {
//        doLast {
//
//            translateJavaFile(getBuildList().get(0))
//
//        }
//    }

}