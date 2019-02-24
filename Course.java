interface IList<T> {
  <R> R accept(IListVisitor<T, R> visitor);
}

class MtList<T> implements IList<T> {
  public <R> R accept(IListVisitor<T, R> visitor) {
    return visitor.forMt(this);
  }
}

class ConsList<T> implements IList<T> {
  T first;
  IList<T> rest;

  ConsList(T first, IList<T> rest) {
    this.first = first;
    this.rest = rest;
  }

  public <R> R accept(IListVisitor<T, R> visitor) {
    return visitor.forCons(this);
  }
}

class Course {
  String name;
  IList<Course> prereqs;

  Course(String name, IList<Course> prereqs) {
    this.name = name;
    this.prereqs = prereqs;
  }

  int getDeepestPathLength() {
    return this.prereqs.accept(new DeepestPathLength());
  }

  boolean hasPrereq(String target) {
    return this.prereqs.accept(new HasPrereq(target));
    // .hasClass(target);
  }

  boolean hasName(String target) {
    return this.name.equals(target) || this.hasPrereq(target);
    // The current prereq's name and it's prereqs
  }
}

interface IFunc<A, R> {
  R apply(A arg);
}

interface IListVisitor<T, R> extends IFunc<IList<T>, R> {
  R forMt(MtList<T> arg);

  R forCons(ConsList<T> consList);
}

class DeepestPathLength implements IListVisitor<Course, Integer> {
  public Integer apply(IList<Course> arg) {
    return arg.accept(this);
  }

  public Integer forMt(MtList<Course> arg) {
    return 0;
  }

  public Integer forCons(ConsList<Course> arg) {
    return Math.max(1 + arg.first.getDeepestPathLength(), arg.rest.accept(new DeepestPathLength()));
  }
}

interface IPred<X> extends IFunc<IList<Course>, Boolean> {
  Boolean apply(IList<Course> prereqs);
}

class HasPrereq implements IPred<Course>, IListVisitor<Course, Boolean> {
  String target;

  HasPrereq(String target) {
    this.target = target;
  }

  public Boolean apply(IList<Course> arg) {
    return false;
  }

  public Boolean forMt(MtList<Course> arg) {
    return true;
  }

  public Boolean forCons(ConsList<Course> arg) {
    return false;
  }
}

class ExamplesCourses {
}
