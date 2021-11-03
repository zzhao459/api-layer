// import React from 'react';
// import { useForm } from 'react-hook-form';
// import { yupResolver } from '@hookform/resolvers/yup';
// import * as Yup from 'yup';
// import "./VerifyAccessPanel.css"
// import { Typography } from '@material-ui/core'
import React, { useState, useMemo } from "react";
import { render } from "react-dom";
import Styles from "./Style";
import { useForm, useField } from "react-final-form-hooks";

function VerifyAccessPanel() {
    const sleep = ms => new Promise(resolve => setTimeout(resolve, ms));

    const onSubmit = async values => {
        await sleep(300);
        window.alert(JSON.stringify(values, 0, 2));
    };
    const validate = values => {
        const errors = {};
        if (!values.url) {
            errors.url = "Required";
        }
        if (!values.lastName) {
            errors.lastName = "Required";
        }
        return errors;
    };

    const initialValues = useMemo(
            () => ({
                url: "https://localhost:8080",
            }),
            []
    );

        const {form, handleSubmit, values, pristine, submitting} = useForm({
            onSubmit,
            initialValues,
            validate
        });

        const url = useField("url", form);

        return (
            <Styles>
                <h2> Verify access panel</h2>
                <form onSubmit={handleSubmit}>
                    <div>
                        <label>Service URL</label>
                        <input {...url.input} placeholder="First Name"/>
                        {url.meta.touched && url.meta.error && (
                            <span>{url.meta.error}</span>
                        )}
                    </div>

                    <div className="buttons">
                        <button type="submit" disabled={submitting}>
                            Verify
                        </button>
                        <button
                            type="button"
                            onClick={() => form.reset()}
                            disabled={submitting || pristine}
                        >
                            Reset
                        </button>
                    </div>
                    <pre>
                        LOG MESSAGES:
                        <br/>
                        </pre>
                </form>
            </Styles>
        );

}
//     const validationSchema = Yup.object().shape({
//         url: Yup.string().required('URL is required')
//     });
//
//     const {
//         form,
//         register,
//         handleSubmit,
//         reset,
//         formState: { errors }
//     } = useForm({
//         resolver: yupResolver(validationSchema),
//         mode: "all"
//     });
//
//     const onSubmit = data => {
//         console.log(JSON.stringify(data, null, 2));
//     };
//
//     return (
//         <div className="register-form">
//             <Typography variant="h4" component="h2">
//                 Verify access to a service
//             </Typography>
//             <br/>
//             <br/>
//             <br/>
//             <form onSubmit={handleSubmit(onSubmit)}>
//                 <div className="form-group">
//                     <label> Service URL </label>
//                     <input
//                         name="url"
//                         type="text"
//                         {...register('url')}
//                         className={`form-control ${errors.url ? 'is-invalid' : ''}`}
//                     />
//                     <div className="invalid-feedback">{errors.url?.message}</div>
//                 </div>
//                 <br/>
//                 <div className="form-group">
//                     <button type="submit" className="btn btn-primary">
//                         Verify
//                     </button>
//                     <button
//                         type="button"
//                         onClick={form.reset()}
//                         className="btn btn-warning float-right"
//                     >
//                         Reset
//                     </button>
//                 </div>
//             </form>
//         </div>
//     );
// }


export default VerifyAccessPanel;


