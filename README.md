# lms-canvas-redirect

To Debug w/ Intellij, forward 5005 (in kube-forwarder, or k9s) to any desired port and then hook intellij up to that

```
helm upgrade redirect harbor-prd/k8s-boot -f helm-common.yaml -f helm-dev.yaml --install
```

```
helm upgrade redirect harbor-prd/k8s-boot -f helm-common.yaml -f helm-snd.yaml --install
```

Base version:
```
mvn clean install spring-boot:run -Dspring-boot.run.jvmArguments="-Dapp.fullFilePath=file:/opt/j2ee/security/lms_poc/base -Dspring.profiles.active=dev,vault -Dlogging.level.edu.iu.uits.lms=DEBUG"
```
With debugging:
```
mvn clean install spring-boot:run -Dspring-boot.run.jvmArguments="-Dapp.fullFilePath=file:/opt/j2ee/security/lms_poc/base -Dspring.profiles.active=dev,vault -Dlogging.level.edu.iu.uits.lms=DEBUG -Dlogging.level.uk.ac.ox.ctl.lti13=DEBUG -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5005"
```

Custom service version:
```
mvn clean install -P var-repl-ser -Dvariable-replacement-service.groupId=edu.iu.uits.lms -Dvariable-replacement-service.artifactId=lms-iu-variable-replacement-service -Dvariable-replacement-service.version=5.0.0 \
    spring-boot:run -Dspring-boot.run.jvmArguments="-Dapp.fullFilePath=file:/opt/j2ee/security/lms_poc/base -Dspring.profiles.active=dev,vault -Dlogging.level.edu.iu.uits.lms=DEBUG -Dapp.customServicePackage=edu.iu.uits.lms.variablereplacement"

```