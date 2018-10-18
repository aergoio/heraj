# Annotation

There are two type of major annotaions
```
@ApiAudience
@ApiStability
```

### @ApiAudience
`@ApiAudience` have two types.
```
@ApiAudience.Public
@ApiAudience.Private
```
`@ApiAudience.Public` means that any classes or methods annotated with it is intended for use on any project or application using heraj.  
`@ApiAudience.Private` means that any classes or methods annotated with it is intended for use within heraj. You may use it but it's not guaranteed.


### @ApiStability
`@ApiStability` have two types.
```
@ApiStability.Stable
@ApiStability.UnStable
```
`@ApiStability.Stable` means that any classes or methods annotated with it evolve while retaining compatibility for minor release boundaries and can break compatibility only for major release (ie. at m.0).  
`@ApiStability.UnStable` means that any classes or methods annotated with it has no guarantee for reliability or stability across any level of release granularity.
