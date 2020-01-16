package com.nullwolf.mubutterknife_compiler;

import com.google.auto.service.AutoService;
import com.nullwolf.mubutterknife_annotations.BindView;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;

@AutoService(Processor.class)
public class MuButterKnifeProcessor extends AbstractProcessor {
    private Elements elementUtils;
    private Filer filer;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);
        elementUtils = processingEnvironment.getElementUtils();
        filer = processingEnvironment.getFiler();
    }

    //指定SourceVersion
    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.RELEASE_7;
    }

    //指定processorType
    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> types = new LinkedHashSet<>();
        Set<Class<? extends Annotation>> supportedAnnotations = getSupportAnnotations();
        for (Class<? extends Annotation> supprotedAnnotation : supportedAnnotations) {
            types.add(supprotedAnnotation.getCanonicalName());
        }
        return types;
    }

    private Set<Class<? extends Annotation>> getSupportAnnotations() {
        Set<Class<? extends Annotation>> annotations = new LinkedHashSet<>();
        annotations.add(BindView.class);
        return annotations;
    }

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        Set<? extends Element> elements = roundEnvironment.getElementsAnnotatedWith(BindView.class);

        //将获取到的bindview细分到每个class
        Map<Element, List<Element>> elementListMap = new LinkedHashMap<>();

        for (Element element : elements) {
            //返回Activity
            Element enclosingElement = element.getEnclosingElement();

            List<Element> bindViewsElements = elementListMap.get(enclosingElement);
            if (bindViewsElements == null) {
                bindViewsElements = new ArrayList<>();
                elementListMap.put(enclosingElement, bindViewsElements);
            }
            bindViewsElements.add(element);
        }

        //生成代码
        for (Map.Entry<Element, List<Element>> entrySet : elementListMap.entrySet()) {
            Element enclosingElement = entrySet.getKey();
            List<Element> bindViewsElements = entrySet.getValue();


            String activityClassNameStr = enclosingElement.getSimpleName().toString();
            ClassName activityClassName = ClassName.bestGuess(activityClassNameStr);
            ClassName unBinderClassName = ClassName.get("com.nullwolf.mubutterknife", "Unbinder");
            TypeSpec.Builder classBuilder =
                    TypeSpec.classBuilder(activityClassNameStr + "_ViewBinding")
                            .addModifiers(Modifier.FINAL, Modifier.PUBLIC)
                            .addSuperinterface(unBinderClassName)
                            .addField(activityClassName, "target", Modifier.PRIVATE);

            //unbind()
            ClassName callSuperClassName = ClassName.get("androidx.annotation", "CallSuper");
            MethodSpec.Builder unbindMethodBuilder = MethodSpec.methodBuilder("unbind")
                    .addAnnotation(Override.class)
                    .addAnnotation(callSuperClassName)
                    .addModifiers(Modifier.PUBLIC, Modifier.FINAL);

            //构建函数
            MethodSpec.Builder constructorMethodBuilder = MethodSpec.constructorBuilder()
                    .addParameter(activityClassName, "target")
                    .addModifiers(Modifier.PUBLIC)
                    .addStatement("this.target = target");

            for (Element bindViewElement : bindViewsElements) {
                //view
                String fieldName = bindViewElement.getSimpleName().toString();
                //Utils
                ClassName utilClassName = ClassName.get("com.nullwolf.mubutterknife", "Utils");
                //获取view的id viewId
                int resourceId = bindViewElement.getAnnotation(BindView.class).value();
                //target.view = Utils.findViewById(target, R.id.viewId)
                constructorMethodBuilder.addStatement("target.$L = $T.findViewById(target,$L)", fieldName, utilClassName, resourceId);
                //target.view = null
                unbindMethodBuilder.addStatement("target.$L = null", fieldName);
            }

            classBuilder.addMethod(unbindMethodBuilder.build())
                    .addMethod(constructorMethodBuilder.build());

            // 获取包名
            String packageName = elementUtils.getPackageOf(enclosingElement).getQualifiedName().toString();

            try {
                JavaFile.builder(packageName, classBuilder.build()).build().writeTo(filer);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return false;
    }
}
