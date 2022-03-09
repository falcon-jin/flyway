package org.flywaydb.core.internal;

import lombok.AccessLevel;
import lombok.CustomLog;
import lombok.NoArgsConstructor;
import org.flywaydb.core.api.FlywayException;
import org.flywaydb.core.internal.license.Edition;
import org.flywaydb.core.internal.license.VersionPrinter;
import org.flywaydb.core.internal.util.ClassUtils;

@CustomLog
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class FlywayTeamsObjectResolver {
    public static <T> T resolve(Class<T> clazz, Object... params) {
        String packageName = clazz.getPackage().getName();
        String className = clazz.getSimpleName();

        // Using instance class loader rather than static because it's more reliable
        // https://github.com/flyway/flyway/issues/3177
        @SuppressWarnings({"InstantiationOfUtilityClass", "InstantiatingObjectToGetClassObject"})
        ClassLoader classLoader = new FlywayTeamsObjectResolver().getClass().getClassLoader();

        if (VersionPrinter.EDITION == Edition.COMMUNITY) {
            return loadCommunityClass(packageName + "." + className, classLoader, params);
        }

        if (VersionPrinter.EDITION == Edition.PRO || VersionPrinter.EDITION == Edition.ENTERPRISE) {
            String pathOfTheClass = packageName + ".teams." + className;
            return loadClass(pathOfTheClass, packageName, className, classLoader, params);
        }

        String pathOfTheClass = packageName + "." + VersionPrinter.EDITION.name().toLowerCase() + "." + className;
        return loadClass(pathOfTheClass, packageName, className, classLoader, params);
    }

    private static <T> T loadClass(String pathOfTheClass, String packageName, String className, ClassLoader classLoader, Object... params) {
        try {
            return ClassUtils.instantiate(pathOfTheClass, classLoader, params);
        } catch (FlywayException e) {
            LOG.debug(e.getMessage() + ". Defaulting to Community Edition for " + className);
            return loadCommunityClass(packageName + "." + className, classLoader, params);
        }
    }

    private static <T> T loadCommunityClass(String pathOfTheClass, ClassLoader classLoader, Object... params) {
        return ClassUtils.instantiate(pathOfTheClass, classLoader, params);
    }
}