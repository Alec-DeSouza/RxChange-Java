# RxChange-Java - Reactive Change Model for Java
RxChange-Java is a library that implements the reactive change model for Java. It was designed to simplify the code required to update application components (e.g. UI, database) based on changes in data, while maintaining consistency and reliability.

Under the hood, the library uses [RxJava](https://github.com/ReactiveX/RxJava) for publishing and subscribing to change events and [Guava](https://github.com/google/guava) to provide immutable snapshots for collections of data.

## Architecture

![Architecture Diagram](diagrams/Architecture.png)

The architecture for the reactive change model consists of 3 main components:
- Data Layer
- Change Layer
- Message Layer

#### Data Layer

The data layer is where the underlying data is stored. This data will be accessed and modified throughout a program's lifespan.

It is important to note that the data cannot be modified directly with the reactive change model, as it must be done through the change layer.

#### Change Layer

The change layer is the layer in which the data is actually modified.  An adapter is used by the caller to either retrieve or modify the underlying data. For each successful change in the data, a change message will be emitted to interested observers.

The reactive change model supports both individual changes (e.g. adding a single element in a list) and batch changes (e.g. adding a multiple elements at a time).

#### Message Layer

The message layer is where the change messages are emitted. It is also the layer where the observers subscribe to and act upon these change events. Each change message contains snapshots of the data (both before and after the change), the type of change that occurred, and the metadata pertaining to the change itself (e.g. the element that was added).

The reactive change model supports 3 types of data changes: add, remove, and update.

## Usage

### Dependencies

[![Maven Central | JRE](https://img.shields.io/maven-central/v/com.umbraltech/rxchange-java/1.1.0-jre.svg)](https://mvnrepository.com/artifact/com.umbraltech/rxchange-java/1.1.0-jre)
[![Maven Central | Android](https://img.shields.io/maven-central/v/com.umbraltech/rxchange-java/1.1.0-android.svg)](https://mvnrepository.com/artifact/com.umbraltech/rxchange-java/1.1.0-android)

#### Gradle

```gradle
// For standard Java projects
implementation 'com.umbraltech:rxchange-java:x.y.z-jre'

// For Android projects
implementation 'com.umbraltech:rxchange-java:x.y.z-android'
```

#### Maven

```xml
<!-- For standard Java projects -->
<dependency>
    <groupId>com.umbraltech</groupId>
    <artifactId>rxchange-java</artifactId>
    <version>x.y.z-jre</version>
</dependency>

<!-- For Android projects -->
<dependency>
    <groupId>com.umbraltech</groupId>
    <artifactId>rxchange-java</artifactId>
    <version>x.y.z-android</version>
</dependency>
```

### Built-In Adapters

The following adapters are packaged with the RxChange-Java library. Information regarding supported operations can be seen in the table below.


| Adapter              | Corresponding Data    | Change Operations              | Metadata Available? |
| :-----------:        | :-----------:      | -----------                      | :-----------:        |
| SingleChangeAdapter  | Object             | update (data: D)                          | No                   |
| ListChangeAdapter    | List             | add (data: D) <br> addAll (data: List) <br> addAt (index: int, data: D) <br><br> remove (data: D) <br> removeAll (data: List) <br> removeAt (index: int) <br><br> update (index: int, data: D) | Yes         |
| MapChangeAdapter     | Map               | add (key: K, data: D) <br> addAll (entries: Map) <br><br> remove (key: K) <br> removeAll (keys: Set) <br><br> update (key: K, data: D) <br> updateAll (entries: Map)                | Yes
| SetChangeAdapter     | Set              | add (data: D) <br> addAll (data: Set) <br><br> remove (data: D) <br> removeAll (data: Set)                  | Yes

### Change Events

In order to listen to change events, an observer must be registered with the adapter responsible for the data. The examples below include code for registering, filtering, and reading data from these change events.

To maintain simplicity, all the code samples will use a `ListChangeAdapter` with data of type `Integer`.

#### Registering Observers

RxChange-Java uses RxJava for listening to change events. The sample code below demonstrates how an observer can be registered with a change adapter.

```Java
    listChangeAdapter.getObservable()
                .subscribe(changeMessage -> /* Logic */ );
```

#### Reading Data

The `ChangeMessage` class provides 3 accessors:
- `getOldPayload()` - returns the data before the change
- `getNewPayload()` - returns the data after the change
- `getChangeType()` - returns the type of change that occurred with the data

#### Reading Metadata

The `MetaChangeMessage` class is an extension of the `ChangeMessage` class, and can be used to read the metadata corresponding to a change in data. In order to access the metadata, simply call `getMetadata()` on a `MetaChangeMessage` instance.

For the collections provided by the library, you can acquire access to the `MetaChangeMessage` instance simply by casting or mapping like in the sample code below.

```Java
    listChangeAdapter.getObservable()
                .map(changeMessage -> (MetaChangeMessage<List<Integer>, Integer>) changeMessage)
                .subscribe(metaChangeMessage -> /* Logic */ );
```

For more information on what metadata values are provided, please refer to the documentation for the adapters.

#### Applying Filters

RxChange-Java comes with bundled with filters that can be used while registering observers, so that the code contained in the observers will only be triggered when certain conditions are met.

##### Change Type Filter

The `ChangeTypeFilter` class allows for listening to changes of a specific type. The example below registers an observer whose logic is only invoked when data is added to the adapter.

```Java
    listChangeAdapter.getObservable()
                .filter(new ChangeTypeFilter(ChangeType.ADD))
                .subscribe(changeMessage -> /* Logic */ );
```

##### Metadata Filter

The `MetadataFilter` class allows executing logic only when certain types of metadata are provided.

Using metadata filters can be especially useful if an adapter supports performing both single and batch updates. Information on the types of metadata supported by the built-in adapters are mentioned in the documentation.

The examples below demonstrate how filtering can be done for both single and batch operations (for a list adapter).

###### Single Change Filter

```Java
    listChangeAdapter.getObservable()
                .map(changeMessage -> (MetaChangeMessage<List<Integer>, Integer>) changeMessage)
                .filter(new MetadataFilter(Integer.class))
                .subscribe(metaChangeMessage -> /* Logic */ );
```

###### Batch Change Filter

```Java
    listChangeAdapter.getObservable()
                .map(changeMessage -> (MetaChangeMessage<List<Integer>, Integer>) changeMessage)
                .filter(new MetadataFilter(List.class))
                .subscribe(metaChangeMessage -> /* Logic */ );
```

#### Example

The following example combines all of the segments listed above into a simple example that prints the values contained in each change message when data is added to the adapter:

Code:

```Java
    final ListChangeAdapter<Integer> listChangeAdapter = new ListChangeAdapter<>();

    listChangeAdapter.getObservable()
            .filter(new ChangeTypeFilter(ChangeType.ADD))
            .filter(new MetadataFilter(Integer.class))
            .map(changeMessage -> (MetaChangeMessage<List<Integer>, Integer>) changeMessage)
            .subscribe(metaChangeMessage -> {
                System.out.println("---- Observer 1 (Single) ----");
                System.out.println("Old List: " + metaChangeMessage.getOldData());
                System.out.println("New List: " + metaChangeMessage.getNewData());
                System.out.println("Metadata: " + metaChangeMessage.getMetadata());
            });

    listChangeAdapter.getObservable()
            .filter(new ChangeTypeFilter(ChangeType.ADD))
            .filter(new MetadataFilter(List.class))
            .map(changeMessage -> (MetaChangeMessage<List<Integer>, List<Integer>>) changeMessage)
            .subscribe(metaChangeMessage -> {
                System.out.println("---- Observer 2 (Batch) ----");
                System.out.println("Old List: " + metaChangeMessage.getOldData());
                System.out.println("New List: " + metaChangeMessage.getNewData());
                System.out.println("Metadata: " + metaChangeMessage.getMetadata());
            });

    // Add the single integer to the dataset
    listChangeAdapter.add(1);

    final List<Integer> batchIntegers = new ArrayList<>();
    batchIntegers.add(2);
    batchIntegers.add(3);
    batchIntegers.add(4);

    // Add the list of integers to the dataset
    listChangeAdapter.addAll(batchIntegers);
```

Output:

```
    ---- Observer 1 (Single) ----
    Old List: []
    New List: [1]
    Metadata: 1
    ---- Observer 2 (Batch) ----
    Old List: [1]
    New List: [1, 2, 3, 4]
    Metadata: [2, 3, 4]
```


## Additional Topics

### Lifecycle Awareness (Android)

When developing Android applications, it may be the case that observers need to be aware of an Activity or Fragment's lifecycle. We recommend using the [AutoDispose](https://uber.github.io/AutoDispose/) library to achieve this purpose.

## Documentation

- [Javadoc](https://alec-desouza.github.io/RxChange-Java/)

## See Also

- [RxChange-Kotlin](https://github.com/Alec-DeSouza/RxChange-Kotlin)
- [RxChange-Android-Demo (Java)](https://github.com/Alec-DeSouza/RxChange-Android-Demo/)