package org.flywaydb.core.internal.resource;

public enum ResourceType {
    MIGRATION,




    REPEATABLE_MIGRATION,
    CALLBACK;

    /**
     * Whether the given resource type represents a resource that is versioned.
     */
    public static boolean isVersioned(ResourceType type) {
        return type == ResourceType.MIGRATION




                ;
    }
}