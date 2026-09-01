package com.vestriamc.basiclore.cloud;

import org.checkerframework.framework.qual.DefaultQualifier;
import org.incendo.cloud.paper.util.sender.Source;
import org.incendo.cloud.parser.ArgumentParser;
import org.incendo.cloud.parser.ParserDescriptor;
import org.jspecify.annotations.NonNull;

@DefaultQualifier(NonNull.class)
public interface DescribedArgumentParser<T> extends
        ParserDescriptor<Source, T>,
        ArgumentParser<Source, T> {

    @Override
    default ArgumentParser<Source, T> parser() {
        return this;
    }
}
