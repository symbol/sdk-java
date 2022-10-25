
/**
* ${helper.capitalize_first_character(generator.comments)}
**/
export enum ${generator.generated_class_name}  {

% for i, (name, (value, comment)) in enumerate(generator.enum_values.items()):
    /** ${comment}. */
    ${name} = ${value},

% endfor
}
