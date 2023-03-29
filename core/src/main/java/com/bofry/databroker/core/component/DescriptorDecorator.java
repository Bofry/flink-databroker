package com.bofry.databroker.core.component;

public class DescriptorDecorator extends BaseDescriptor implements IObjectDescriptor {

    private final IObjectDescriptor subject;

    public DescriptorDecorator(IObjectDescriptor subject) {
        this.subject = subject;
    }

    @Override
    public String get(String key) {
        String descriptor = null;
        if (subject != null) {
            descriptor = subject.get(key);
        }
        if (descriptor == null) {
            descriptor = super.get(key);
        }
        return descriptor;
    }

}
