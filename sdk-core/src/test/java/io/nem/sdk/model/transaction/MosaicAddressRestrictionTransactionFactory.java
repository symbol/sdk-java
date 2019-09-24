package io.nem.sdk.model.transaction;

import io.nem.sdk.model.account.Address;
import io.nem.sdk.model.blockchain.NetworkType;
import io.nem.sdk.model.mosaic.MosaicId;
import java.math.BigInteger;
import org.apache.commons.lang3.Validate;

public class MosaicAddressRestrictionTransactionFactory extends
    TransactionFactory<MosaicAddressRestrictionTransaction> {

  private final MosaicId unresolvedMosaicId;
  private final BigInteger restrictionKey;
  private final Address targetAddress;
  private final BigInteger previousRestrictionValue;
  private final BigInteger newRestrictionValue;

  public MosaicAddressRestrictionTransactionFactory(
      NetworkType networkType, MosaicId unresolvedMosaicId, BigInteger restrictionKey,
      Address targetAddress, BigInteger previousRestrictionValue, BigInteger newRestrictionValue) {
      super(TransactionType.MOSAIC_ADDRESS_RESTRICTION, networkType);
      Validate.notNull(unresolvedMosaicId, "UnresolvedMosaicId must not be null");
    Validate.notNull(restrictionKey, "RestrictionKey must not be null");
    Validate.notNull(targetAddress, "TargetAddress must not be null");
    Validate.notNull(previousRestrictionValue, "PreviousRestrictionValue must not be null");
    Validate.notNull(newRestrictionValue, "NewRestrictionValue must not be null");
      this.unresolvedMosaicId = unresolvedMosaicId;
      this.restrictionKey = restrictionKey;
      this.targetAddress = targetAddress;
      this.previousRestrictionValue = previousRestrictionValue;
      this.newRestrictionValue = newRestrictionValue;
  }

  @Override
  public MosaicAddressRestrictionTransaction build() {
    return new MosaicAddressRestrictionTransaction( this);
  }

  public MosaicId getUnresolvedMosaicId() {
    return unresolvedMosaicId;
  }

  public BigInteger getRestrictionKey() {
    return restrictionKey;
  }

  public Address getTargetAddress() {
    return targetAddress;
  }

  public BigInteger getPreviousRestrictionValue() {
    return previousRestrictionValue;
  }

  public BigInteger getNewRestrictionValue() {
    return newRestrictionValue;
  }
}
