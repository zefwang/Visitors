import tester.Tester;

interface IList<T> {
  // Accepts a visitor and determines if the IList is Mt or Cons
  <R> R accept(IListVisitor<T, R> visitor);
}

class MtList<T> implements IList<T> {
  // Accepts a visitor and calls the method for MtList
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

  // Accepts a visitor and calls the method for ConsList
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

  // Returns the longest path of courses with prereqs
  int getDeepestPathLength() {
    return new DeepestPathLength().apply(this);
  }

  // Determines if a course has a prereq with the given name
  boolean hasPrereq(String target) {
    return new HasPrereq(target).apply(this);
  }

  // Determines if this course or its prereqs is the target
  boolean hasName(String target) {
    return this.name.equals(target) || this.hasPrereq(target);
    // The current prereq's name and it's prereqs
  }

  boolean hasPrereqOrMap(String target) {
    return new OrMap(new SameName(target)).apply(this);
  }
}

interface IFunc<A, R> {
  // Applies a function that goes from A->R
  R apply(A arg);
}

interface IListVisitor<T, R> extends IFunc<T, R> {
  // Generic function for an MtList
  R forMt(MtList<T> arg);

  // Generic function for a ConsList
  R forCons(ConsList<T> consList);
}

class DeepestPathLength implements IListVisitor<Course, Integer> {
  // Returns zero because cannot go any deeper
  public Integer forMt(MtList<Course> arg) {
    return 0;
  }

  // Returns the deepest path length
  public Integer forCons(ConsList<Course> arg) {
    return Math.max(1 + arg.first.getDeepestPathLength(), arg.rest.accept(new DeepestPathLength()));
  }

  // Determines if the list is empty or cons using a visitor
  public Integer apply(Course arg) {
    return arg.prereqs.accept(this);
  }
}

interface IPred<X> extends IFunc<X, Boolean> {
}

class HasPrereq implements IPred<Course> {
  String target;

  // A HasPrereq instance takes in the target course name
  HasPrereq(String target) {
    this.target = target;
  }

  // Method calls the helper to determine if list is Mt or Cons
  public Boolean apply(Course arg) {
    return new HasReqHelper(this.target).apply(arg);
  }
}

// The IListVisitor that does all the work.
class HasReqHelper implements IListVisitor<Course, Boolean> {
  String target;

  HasReqHelper(String target) {
    this.target = target;
  }

  // A visitor that determines if list is Mt or Cons
  public Boolean apply(Course arg) {
    return arg.prereqs.accept(this);
  }

  // Returns false because the target is not in the empty list
  public Boolean forMt(MtList<Course> arg) {
    return false;
  }

  // Determines if the list of prereqs contains the string with the given target
  public Boolean forCons(ConsList<Course> arg) {
    return arg.first.hasName(target) || arg.rest.accept(this);
  }
}

// The generic OrMap that takes an IPred<X> to check against a list.
class OrMap<T> implements IListVisitor<Course, Boolean> {
  IPred<Course> predicate;

  OrMap(IPred<Course> predicate) {
    this.predicate = predicate;
  }

  public Boolean apply(Course arg) {
    return arg.prereqs.accept(this);
  }

  public Boolean forMt(MtList<Course> arg) {
    return false;
  }

  public Boolean forCons(ConsList<Course> arg) {
    return this.predicate.apply(arg.first) || arg.rest.accept(this);
  }
}

// The IPred<X> that would fed to OrMap to imitate HasPrereq
class SameName implements IPred<Course> {
  String name;

  SameName(String name) {
    this.name = name;
  }

  public Boolean apply(Course arg) {
    return arg.name.equals(this.name) || new OrMap(new SameName(this.name)).apply(arg);
  }
}

class ExamplesCourses {
  MtList<Course> empty = new MtList<Course>();
  Course c1 = new Course("CS 1", empty);
  Course c2 = new Course("CS 2", empty);
  ConsList<Course> cList1And2 = new ConsList<Course>(c2, new ConsList<Course>(c1, empty));
  Course c3 = new Course("CS 3", cList1And2);
  Course c4 = new Course("CS 4", empty);
  Course c5 = new Course("CS 5", empty);
  ConsList<Course> cList34And5 = new ConsList<Course>(c3,
      new ConsList<Course>(c4, new ConsList<Course>(c5, empty)));
  Course c6 = new Course("CS 6", cList34And5);
  Course c7 = new Course("CS 7", new ConsList<Course>(c6, empty));

  // Tests for the getDeepestPathLength() method
  boolean testDeepestPathLength(Tester t) {
    return t.checkExpect(c1.getDeepestPathLength(), 0)
        && t.checkExpect(c3.getDeepestPathLength(), 1)
        && t.checkExpect(c6.getDeepestPathLength(), 2)
        && t.checkExpect(c4.getDeepestPathLength(), 0)
        && t.checkExpect(c7.getDeepestPathLength(), 3);
  }

  // Tests for the hasPrereq() method
  boolean testHasPrereq(Tester t) {
    return t.checkExpect(c1.hasPrereq("CS 3"), false) && t.checkExpect(c3.hasPrereq("CS 1"), true)
        && t.checkExpect(c3.hasPrereq("CS 4"), false) && t.checkExpect(c4.hasPrereq("CS 1"), false)
        && t.checkExpect(c6.hasPrereq("CS 1"), true) && t.checkExpect(c7.hasPrereq("CS 5"), true)
        && t.checkExpect(c7.hasPrereq("CS 2"), true) && t.checkExpect(c7.hasPrereq("CS 0"), false);
  }

  // Tests for the hasName() method
  boolean testHasName(Tester t) {
    return t.checkExpect(c1.hasName("CS 1"), true) && t.checkExpect(c1.hasName("CS 2"), false)
        && t.checkExpect(c3.hasName("CS 1"), true) && t.checkExpect(c3.hasName("CS 4"), false)
        && t.checkExpect(c5.hasName("CS 1"), false) && t.checkExpect(c6.hasName("CS 2"), true)
        && t.checkExpect(c7.hasName("CS 5"), true) && t.checkExpect(c7.hasName("CS 8"), false);
  }

  // Tests for the accept() method
  boolean testAccept(Tester t) {
    return t.checkExpect(empty.accept(new DeepestPathLength()), 0)
        && t.checkExpect(cList1And2.accept(new DeepestPathLength()), 1)
        && t.checkExpect(cList34And5.accept(new DeepestPathLength()), 2);
  }

  boolean testApply(Tester t) {
    return t.checkExpect(new DeepestPathLength().apply(c1), 0)
        && t.checkExpect(new DeepestPathLength().apply(c7), 3)
        && t.checkExpect(new HasPrereq("CS 1").apply(c2), false)
        && t.checkExpect(new HasPrereq("CS 1").apply(c7), true)
        && t.checkExpect(new HasPrereq("CS 8").apply(c7), false);
  }

  boolean testForMt(Tester t) {
    return t.checkExpect(new DeepestPathLength().forMt(empty), 0)
        && t.checkExpect(new HasReqHelper("CS 1").forMt(empty), false);
    // forMt() is not in HasPrereq because we used a helper class
  }

  boolean testForCons(Tester t) {
    return t.checkExpect(new DeepestPathLength().forCons(cList1And2), 1)
        && t.checkExpect(new DeepestPathLength().forCons(cList34And5), 2)
        && t.checkExpect(new HasReqHelper("CS 1").forCons(cList1And2), true)
        && t.checkExpect(new HasReqHelper("CS 2").forCons(cList34And5), true);
  }
  // tests for the OrMap implementation of HasPrereq

  boolean testOrMap(Tester t) {
    return t.checkExpect(this.c7.hasPrereqOrMap("CS 1"), true)
        && t.checkExpect(this.c2.hasPrereqOrMap("CS 2"), false)
        && t.checkExpect(this.c3.hasPrereqOrMap("CS 1"), true)
        && t.checkExpect(this.c4.hasPrereqOrMap("CS 1"), false)
        && t.checkExpect(this.c6.hasPrereqOrMap("CS 1"), true)
        && t.checkExpect(this.c6.hasPrereqOrMap("CS 5"), true)
        && t.checkExpect(this.c7.hasPrereqOrMap("CS 5"), true)
        && t.checkExpect(this.c5.hasPrereqOrMap("CS 2"), false);
  }

}
