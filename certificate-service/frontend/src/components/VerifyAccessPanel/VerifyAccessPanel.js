import React, {  useMemo } from "react";
import Styles from "./Style";
import { styled } from '@mui/material/styles';
import { useForm, useField } from "react-final-form-hooks";
import Button from '@mui/material/Button';
import Stack from '@mui/material/Stack';
import './VerifyAccessPanel.css'
function VerifyAccessPanel() {
    const Input = styled('input')({
        display: 'none',
    });
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

        const {form, handleSubmit, pristine, submitting} = useForm({
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
                    <Stack direction="row" alignItems="center" spacing={2}>
                        <label htmlFor="contained-button-file">
                            <Input accept="image/*" id="contained-button-file" multiple type="file" />
                            <Button variant="contained" component="span">
                                Upload Certificate
                            </Button>
                        </label>
                    </Stack>
                    <pre>
                        LOG MESSAGES: error from backend
                        <br/>
                        </pre>
                </form>
            </Styles>
        );

}


export default VerifyAccessPanel;


