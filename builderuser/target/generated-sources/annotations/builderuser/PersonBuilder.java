package builderuser;

import javax.annotation.Generated;
import net.karneim.pojobuilder.GwtIncompatible;

@Generated("PojoBuilder")
public class PersonBuilder
    implements Cloneable {
  protected PersonBuilder self;
  protected String value$name$java$lang$String;
  protected boolean isSet$name$java$lang$String;
  protected int value$alter$int;
  protected boolean isSet$alter$int;

  /**
   * Creates a new {@link PersonBuilder}.
   */
  public PersonBuilder() {
    self = (PersonBuilder)this;
  }

  /**
   * Sets the default value for the name property.
   *
   * @param value the default value
   * @return this builder
   */
  public PersonBuilder withName(String value) {
    this.value$name$java$lang$String = value;
    this.isSet$name$java$lang$String = true;
    return self;
  }

  /**
   * Sets the default value for the alter property.
   *
   * @param value the default value
   * @return this builder
   */
  public PersonBuilder withAlter(int value) {
    this.value$alter$int = value;
    this.isSet$alter$int = true;
    return self;
  }

  /**
   * Returns a clone of this builder.
   *
   * @return the clone
   */
  @Override
  @GwtIncompatible
  public Object clone() {
    try {
      PersonBuilder result = (PersonBuilder)super.clone();
      result.self = result;
      return result;
    } catch (CloneNotSupportedException e) {
      throw new InternalError(e.getMessage());
    }
  }

  /**
   * Returns a clone of this builder.
   *
   * @return the clone
   */
  @GwtIncompatible
  public PersonBuilder but() {
    return (PersonBuilder)clone();
  }

  /**
   * Creates a new {@link Person} based on this builder's settings.
   *
   * @return the created Person
   */
  public Person build() {
    try {
      Person result = new Person();
      if (isSet$name$java$lang$String) {
        result.setName(value$name$java$lang$String);
      }
      if (isSet$alter$int) {
        result.setAlter(value$alter$int);
      }
      return result;
    } catch (RuntimeException ex) {
      throw ex;
    } catch (Exception ex) {
      throw new RuntimeException(ex);
    }
  }
}
