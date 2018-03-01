package org.bspv.authorization.repository.jooq.converter;

import java.util.UUID;

import org.jooq.Converter;

public class UUIDConverter implements Converter<byte[], UUID> {

    /**
     * 
     */
    private static final long serialVersionUID = 6861967124862772875L;

    /*
     * (non-Javadoc)
     * @see org.jooq.Converter#from(java.lang.Object)
     */
    @Override
    public UUID from(byte[] databaseObject) {
        return UUID.fromString(new String(databaseObject));
    }

    /*
     * (non-Javadoc)
     * @see org.jooq.Converter#to(java.lang.Object)
     */
    @Override
    public byte[] to(UUID userObject) {
        return userObject.toString().getBytes();
    }

    /*
     * (non-Javadoc)
     * @see org.jooq.Converter#fromType()
     */
    @Override
    public Class<byte[]> fromType() {
        return byte[].class;
    }

    /*
     * (non-Javadoc)
     * @see org.jooq.Converter#toType()
     */
    @Override
    public Class<UUID> toType() {
        return UUID.class;
    }

}
