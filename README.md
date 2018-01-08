# RxChange - Reactive Change Model for Java
RxChange is a library that implements the reactive change model for Java. It was designed to simplify the code required to update application components (e.g. UI, database) based on changes in data, while maintaining consistency and reliability.

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

The message layer is where the change messages are emitted. It is also the layer where the observers subscribe to and act upon these change events. Each change message contains snapshots of the data (both before and after the change), in addition to the metadata pertaining to the change itself (e.g. the element that was added).

The reactive change model supports 3 types of data changes: add, remove, and update.

## Usage

### Dependencies

#### Gradle

Instructions coming soon!

#### Maven

Instructions coming soon!

### Built-In Adapters

The following adapters are packaged with the RxChange library. Information regarding supported operations can be seen in the table below.


| Adapter              | Corresponding Data    | Change Operations              | Metadata Available? |
| :-----------:        | :-----------:      | -----------                      | :-----------:        |
| SingleChangeAdapter  | Object             | update (data: D)                          | No                   |
| ListChangeAdapter    | List             | add (data: D) <br> add (data: List) <br> remove (index: int) <br> remove (indices: List) <br> update (index: int, data: D) | Yes         |
| MapChangeAdapter     | Map               | add (key: K, data: D) <br> add (entry: Map) <br> remove (key: K) <br> remove (entries: Map) <br> update (key: K, data: D) <br> update (entries: Map)                | Yes
| SetChangeAdapter     | Set              | add (data: D) <br> add (data: Set) <br> remove (data: D) <br> remove (data: Set)                  | Yes

### Change Events

In order to listen to change events, an observer must be registered with the adapter responsible for the data. The examples below include code for registering, filtering, and reading data from these change events.

To maintain simplicity, all the code samples will use a `ListChangeAdapter` with data of type `Integer`.

#### Registering Observers

RxChange uses RxJava2 for listening to change events. The sample code below demonstrates how an observer can be registered with a change adapter.

```Java
    listChangeAdapter.getObservable()
                .subscribe(changeMessage -> /* Logic */ );
```

#### Reading Data

The `ChangeMessage` class provides 3 accessors:
- `getOldPayload()` - returns a snapshot of the data before the change
- `getNewPayload()` - returns a snapshot of the data after the change
- `getChangeType()` - returns the type of change that occurred with the data

#### Reading Metadata

The `MetaChangeMessage` class is an extension of the `ChangeMessage` class, and can be used to read the metadata corresponding to a change in data. In order to access the metadata, simply call `getMetadata()` on a `MetaChangeMessage` instance.

For more information on what metadata values are provided, please refer to the documentation for the adapters.

```Java
    listChangeAdapter.getObservable()
                .map(changeMessage -> (MetaChangeMessage<List<Integer>, Integer>) changeMessage)
                .subscribe(metaChangeMessage -> /* Logic */ );
```

#### Applying Filters

RxChange comes with bundled with filters that can be used while registering observers, so that the containing will only be triggered when certain conditions are met.

##### Change Type Filter

The `ChangeTypeFilter` class allows listening to changes of a specific type. The example below registers an observer whose logic is only invoked when data is added to the adapter.

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

## Additional Topics

### Lifecycle Awareness (Android)

When developing Android applications, it may be the case that observers need to be aware of an Activity or Fragment's lifecycle. We recommend using the [RxLifecycle](https://github.com/trello/RxLifecycle) library and then having activities extend the `RxActivity` class for activities or the `RxFragment` class for fragments. Afterwards, the `compose()` function can be used in conjunction with RxLifecycle's `bind***()` methods while registering the observer.

The example below registers an observer that will only listen up until the Activity enters the paused state.

```Java

public class MyActivity extends RxActivity {

    // ...

    private void myFunction() {

        // ...

        listChangeAdapter.getObservable()
                    .compose(bindUntilEvent(ActivityEvent.PAUSE))
                    .subscribe(changeMessage -> /* Logic */ );
    }
}
```

## Documentation

- [Javadoc](https://alec-desouza.github.io/RxChange/)

## Examples
- [RxChange-Android-Demo](https://github.com/Alec-DeSouza/RxChange-Android-Demo)