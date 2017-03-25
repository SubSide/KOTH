package subside.plugins.koth.captureentities;

public abstract class CappingGroup<T> extends Capper<T> {

    public CappingGroup(CaptureTypeRegistry captureTypeRegistry, String uniqueClassIdentifier, T object) {
        super(captureTypeRegistry, uniqueClassIdentifier, object);
    }
}
